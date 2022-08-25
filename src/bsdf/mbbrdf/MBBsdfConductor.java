/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.mbbrdf;

import bitmap.spectrum.surface.ConductorFresnel;
import bsdf.geom.Color4_b;
import bsdf.geom.Vector3_b;
import static bsdf.surface.MaterialUtility_b.fresnelConductorExact;
import coordinate.surface.Frame;
import coordinate.surface.MicrofacetGGX;
import static coordinate.utility.Utility.acosf;
import static coordinate.utility.Utility.powf;
import static coordinate.utility.Utility.sinf;
import static coordinate.utility.Utility.sqrtf;
import coordinate.utility.Value1Df;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 *
 * @author user
 */
public class MBBsdfConductor {
    
    int OPTIMIZE_PERFORMANCE = 0;
    float PI_DIVIDE_180 = 0.0174532922f;
    float INV_2_SQRT_M_PI = 0.28209479177387814347f; /* 0.5/sqrt(pi) */
    float THRESHOLD  = 1e-4f;
    int MAX_VERTEX = 10;
    int  FRESNEL = 1;
    
    public float m_alpha_x, m_alpha_y;
    public Color4_b m_eta, m_k;
    public  MicrofacetGGX<Vector3_b> m_distr;
    
    public MBBsdfConductor()
    {
        m_alpha_x = 0;
        m_alpha_y = 0;
        m_distr = new MicrofacetGGX(0, 0);
    }
    
    void getLambda(Vector3_b wi, Vector3_b wo, 
		Value1Df inLambda, Value1Df outLambda)
    {
        RayInfoGGX ray_shadowing = new RayInfoGGX();
        ray_shadowing.updateDirection(wo, m_alpha_x, m_alpha_y);

        RayInfoGGX ray = new RayInfoGGX();
        ray.updateDirection(wi, m_alpha_x, m_alpha_y);

        inLambda.x = ray.Lambda;
        outLambda.x = ray_shadowing.Lambda;
    }
    
    float getInLambda(Vector3_b wi) 
    {
        RayInfoGGX ray = new RayInfoGGX();
        ray.updateDirection(wi.neg(), m_alpha_x, m_alpha_y);
        return abs(ray.Lambda) - 1;       
    }
    
    float getLambda(Vector3_b wo)
    {
        RayInfoGGX ray = new RayInfoGGX();
        ray.updateDirection(wo, m_alpha_x, m_alpha_y);
        return (ray.Lambda);            
    }
    
    //height-correlated G2 for the middle bounce
    float computeG2_cor_middle(Vector3_b wi, Vector3_b wo) 
    {
        Value1Df inLambda = new Value1Df(), outLambda = new Value1Df();
        getLambda(wi, wo, inLambda, outLambda);

        float Gtemp = 1.0f / (abs(1.0f + inLambda.x) + outLambda.x);
        float Gtemp2 = 1.0f / (abs(1.0f + inLambda.x));
        float  G = Gtemp2 - Gtemp;
        return G;
    }
    
    //height-correlated G2 for the last bounce
    float computeG2_cor_last(Vector3_b wi, Vector3_b wo) 
    {
        Value1Df inLambda = new Value1Df(), outLambda = new Value1Df();
        getLambda(wi, wo, inLambda, outLambda);
        float temp = (abs(1.0f + inLambda.x) + outLambda.x);
        float G = abs(temp) < 1e-10f ? 0.0f : 1.0f / temp;
        return  G;
    }
    //height-uncorrelated G2 for the last bounce
    float computeG2_uncor_last(Vector3_b wi, Vector3_b wo)
    {
        Value1Df inLambda = new Value1Df(), outLambda = new Value1Df();
        getLambda(wi, wo, inLambda, outLambda);

        float G11 = 1.0f / abs(1.0f + inLambda.x);
        float G12 = 1.0f / abs(1.0f + outLambda.x);
        float G = G11 * G12;
        return G;
    }

    //G2 for the last bounce (performance optimized version)
    float computeG2_middle_opt(BSDFSamplingRecord bRec, float inLamda, float outLamda)
    {
        float G;

        if (bRec.wo.z < 0)
        {
                G = 1.0f / (1 + inLamda);
        }
        else
        {			
            //uncorrelated
            float G11 = 1.0f / (1 + inLamda);
            float G12 = 1.0f / (1 + outLamda);
            G = G11 * (1 - G12);			
        }
        return G;
    }

    float computeG1(Vector3_b wi) 
    {
        float lambda = getLambda(wi);
        float G11 = 1.0f / abs(1.0f + lambda);
        return G11;
    }

    //G2 for all the cases
    float computeG(Vector3_b wi, Vector3_b wo, boolean outShadow)
    {
        float G;

        if (outShadow)
        {
            //uncorrelated
            G = computeG2_uncor_last(wi, wo);
        }
        else
        {
            if (wo.z < 0)
            {
                    G = computeG1(wi);
            }
            else
            {
                //uncorrelated				
                float G11 = computeG1(wi); 
                float G12 = computeG1(wo); 
                G = G11 * (1 - G12);

            }
        }

        return G;
    }
    
    //vertex term, exept the Jacobian term
    Color4_b computeD_F(Vector3_b wi, Vector3_b wo) 
    {
        if((wi.add(wo)).isZero())
            return new Color4_b();
        
        /* Calculate the reflection half-vector */
        Vector3_b H = wi.add(wo).normalize();
        float D = m_distr.evaluate(H);
        
        if (D == 0)
            return new Color4_b(0.0f);
        
        /* Fresnel factor */
        
        Color4_b F = new Color4_b();
        if(FRESNEL > 0)
        {
            ConductorFresnel<Color4_b> conductor = new ConductorFresnel(m_eta, m_k);
            F = conductor.evaluateSpectrum(wi.dot(H));            
        } 
        return F.mul(D);
    }
    
    //vertex term, exept the Jacobian term, the D wil be cancelled out in the sampling, so do not include here.
    public Color4_b computeD_F_withoutD(Vector3_b wi, Vector3_b wo)
    {
        if ((wo.add(wi)).isZero())
            return new Color4_b(0.0f);

        /* Calculate the reflection half-vector */
        Vector3_b H = wo.add(wi).normalize();
        float D = m_distr.evaluate(H);

        if (D == 0)
            return new Color4_b(0.0f);

        Color4_b F = new Color4_b();
        if(FRESNEL > 0)
        {
            ConductorFresnel<Color4_b> conductor = new ConductorFresnel(m_eta, m_k);
            F = conductor.evaluateSpectrum(wi.dot(H));            
        } 
        return F;
    }
    
    Color4_b evalBounce_opt(BSDFSamplingRecord bRec, boolean outShadow, float inLamda, float outLamda) 
    {
        Color4_b result = computeD_F(bRec.wi, bRec.wo);
        if (result.isBlack()) 
            return new Color4_b(0.0f);

        float G = computeG2_middle_opt(bRec, inLamda, outLamda);
       
        result.mulAssign(G / abs(4.0f * Frame.cosTheta(bRec.wi)));
        return result;
    }
    
    Color4_b evalBounce(Vector3_b wi, Vector3_b wo, boolean outShadow)
    {
        Color4_b result = computeD_F(wi, wo);
        if (result.isBlack()) return new Color4_b(0.0f);

        float G = computeG(wi, wo, outShadow);
        result.mulAssign(G / (4 * abs(wi.z)));

        return result;
    }
    
    float pdfVisible(Vector3_b wi, Vector3_b m) 
    {
        if (Frame.cosTheta(wi) == 0)
                return 0.0f;

        float G1 = computeG1(wi);
        if (!Float.isFinite(G1))
                return 0.0f;

        return  G1* wi.absDot(m) * m_distr.evaluate(m) / Math.abs(Frame.cosTheta(wi));
    }
    
    
    
    class PathSimple
    {
        float pdf[] = new float[MAX_VERTEX];
	float invPdf[] = new float[MAX_VERTEX];
	int count;
        
        PathSimple()
        {
            count = 0;
        }
        
        void add(float _pdf, float _invPdf)
        {
            pdf[count] = _pdf;
            invPdf[count] = _invPdf;
            count++;
        }
    }
    
    class Vertex
    {
        Vector3_b wi = null;
	Vector3_b wo = null;
	Color4_b weight = null;
	Color4_b weightAcc = null;

	float pdf = 0;
	float pdfAcc = 0;
	float invPdf = 0;
	float invPdfAcc = 0;
	float inLamda = 0;
	float outLamda = 0;
        
        Vertex(float _pdf){ pdf = _pdf; }
	Vertex(Vector3_b _wi, Vector3_b _wo, float _pdf, Color4_b _weight,
		Color4_b _specAcc, float _pdfAcc,
		float _inLamda, float _outLamda, float _invPdf, float _invPdfAcc)
	{
		wi = _wi;
		wo = _wo;
		pdf = _pdf;
		weight = _weight;
		weightAcc = _specAcc;
		pdfAcc = _pdfAcc;
		inLamda = _inLamda;
		outLamda = _outLamda;
		invPdf = _invPdf;
		invPdfAcc = _invPdfAcc;
	}
    }
    
    class PathSample
    {
        Vertex vList[] = new Vertex[MAX_VERTEX];
	int count;
        
        PathSample(){ count = 0; }

	void add(Vector3_b _wi, Vector3_b _wo, float _pdf,
		Color4_b _weight,
		Color4_b _specAcc, float _pdfAcc,
		float inLamda, float outLamda, float invPdf, float _invPdfAcc)
	{
		vList[count] = new Vertex(_wi, _wo, _pdf, _weight, _specAcc, _pdfAcc, inLamda, outLamda, invPdf, _invPdfAcc);
		count++;
	}

	void add(float _pdf){
		vList[count] = new Vertex(_pdf);
		count++;
	}
    }
    

    class RayInfoGGX
    {
        // direction
	Vector3_b w;
	float theta;
	float cosTheta;
	float sinTheta;
	float tanTheta;
	float alpha;
	float Lambda;
        
        void updateDirection(Vector3_b w, float alpha_x, float alpha_y)
	{
            this.w = w;
            theta = acosf(w.z);
            cosTheta = w.z;
            sinTheta = sinf(theta);
            tanTheta = sinTheta / cosTheta;
            float invSinTheta2 = 1.0f / (1.0f - w.z*w.z);
            float cosPhi2 = w.x*w.x*invSinTheta2;
            float sinPhi2 = w.y*w.y*invSinTheta2;
            alpha = sqrtf(cosPhi2*alpha_x*alpha_x + sinPhi2*alpha_y*alpha_y);

            // Lambda
            if (w.z > 0.9999f)
                    Lambda = 0.0f;
            else if (w.z < -0.9999f)
                    Lambda = -1.0f;
            else
            {
                    float a = 1.0f / tanTheta / alpha;
                    Lambda = 0.5f*(-1.0f + ((a>0) ? 1.0f : -1.0f) * sqrtf(1 + 1 / (a*a)));
            }
	}
        
        // height
	float h;
	float C1;
	float G1;

	void updateHeight(float h)
	{
            this.h = h;
            C1 = min(1.0f, max(0.0f, 0.5f*(h + 1.0f)));

            if (this.w.z > 0.9999f)
                G1 = 1.0f;
            else if (this.w.z <= 0.0f)
                G1 = 0.0f;
            else
                G1 = powf(this.C1, this.Lambda);
	}
    }
    
    class BSDFSamplingRecord
    {
        Vector3_b wo;
        Vector3_b wi;
        
        public BSDFSamplingRecord(){
            
        }
    }
}
