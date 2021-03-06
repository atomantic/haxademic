package com.haxademic.sketch.render;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.PBlendModes;
import com.haxademic.core.draw.filters.shaders.ChromaColorFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.draw.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.image.HaxMotionBlur;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.draw.image.DrawCommand.Command;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;
import processing.core.PImage;

public class BlackHoleScreens
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected int _frames = 160;
	PGraphics _pg;
	MotionBlurPGraphics _pgMotionBlur;
	PImage[] images;
	PGraphics[] buffers;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 1000 );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, true );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames/4 );
	}

	public void setup() {
		super.setup();
		OpenGLUtil.setTextureQualityHigh(p.g);
		buildMotionBlur();
	}
	
	protected void buildMotionBlur() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_NONE);
		OpenGLUtil.setTextureRepeat(_pg);
		_pgMotionBlur = new MotionBlurPGraphics(4);
	}
	
	protected void initImages() {
		images = new PImage[] {
				p.loadImage(FileUtil.getFile("images/phone-2-trans.png")),
				p.loadImage(FileUtil.getFile("images/phone-2-trans.png")),
				p.loadImage(FileUtil.getFile("images/phone-2-trans.png")),
				p.loadImage(FileUtil.getFile("images/phone-2-trans.png"))
		};
		
		buffers = new PGraphics[4];
		for (int i = 0; i < buffers.length; i++) {
			buffers[i] = p.createGraphics(p.width * 2, p.height * 2, P.P2D);
			ImageUtil.imageToGraphicsCropFill(images[i], buffers[i]);
//			VignetteFilter.instance(p).setDarkness(0.99f);
//			VignetteFilter.instance(p).setSpread(0.7f);
//			VignetteFilter.instance(p).applyTo(buffers[i]);
//			ChromaColorFilter.instance(p).presetBlackKnockout();
//			ChromaColorFilter.instance(p).applyTo(buffers[i]);
		}
	}

	public void drawApp() {
		p.background(255);
		if(p.frameCount == 1) initImages();
		DrawUtil.feedback(p.g, p.color(255, 127), 0.2f, 1f);
		drawFrame(p.g);
//		drawFrame(pg);
//		p.image(_pg, 0, 0);
//		_pgMotionBlur.updateToCanvas(_pg, p.g, 1f);
	}
	
	public void drawFrame(PGraphics pg) {
		pg.beginDraw();
		pg.clear();
		pg.pushMatrix();

		DrawUtil.setDrawCenter(pg);
//		pg.fill(255);
		pg.noFill();
		pg.noStroke();
		pg.translate(pg.width/2, pg.height/2);
		DrawUtil.setDrawCenter(pg);
		DrawUtil.setDrawFlat2d(pg, false);
		pg.blendMode(PBlendModes.BLEND);
		
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
		float radsComplete = percentComplete * P.TWO_PI;
		
//		pg.blendMode(PBlendModes.LIGHTEST);
//		DrawUtil.setPImageAlpha(p, 0.4f);
		
		float numImages = buffers.length;
		float spacing = 400f;
		float startZ = -6000f;
		float alphaFadeDist = 2000f;
		float maxAlpha = 0.75f;
		float showZ = startZ + spacing * numImages;
		
		float curZ = startZ + percentComplete * (spacing * numImages);
		int i = 0;
		
		// draw black holes
		while(curZ < 1000) {
			pg.pushMatrix();
			curZ += spacing;
			pg.translate(0, 0, curZ);
			if(curZ > showZ) {
				float alpha = (curZ < showZ + alphaFadeDist) ? P.map(curZ, showZ, showZ + alphaFadeDist, 0, maxAlpha) : maxAlpha;
				//DrawUtil.setPImageAlpha(p, alpha);
				pg.rotate(P.sin(curZ * 0.0015f));
				pg.scale(1f + 0.83f * P.sin(curZ * 0.0005f)); // P.map(p.mouseX, 0, p.width, 0, 1f)
				pg.image(buffers[(int)i % buffers.length], 0, 0);
			}
			i++;
			pg.popMatrix();
		}
		
		// prep overlay
		DrawUtil.setDrawFlat2d(pg, true);
//		pg.blendMode(PBlendModes.SCREEN);
		
		// draw shapes
		startZ = -3000f;
		spacing /= 2f;
		curZ = startZ + 2 + percentComplete * (spacing * numImages);
		showZ = startZ + spacing * numImages;
		i = 0;
		
		while(curZ < 1000) {
			pg.pushMatrix();
			curZ += spacing;
			pg.translate(0, 0, curZ);
			if(curZ > showZ) {
				float alpha = (curZ < showZ + alphaFadeDist) ? P.map(curZ, showZ, showZ + alphaFadeDist, 0, maxAlpha) : maxAlpha;
				int vertices = 3 + i % 4;
				vertices = 3;
//				pg.rotate(P.sin(curZ * 0.001f * vertices));
				pg.rotate(P.sin(curZ * 0.0015f));
//				pg.scale(1f + 0.83f * P.sin(curZ * 0.001f)); // P.map(p.mouseX, 0, p.width, 0, 1f)
				pg.stroke(255, alpha * 255f * 1.0f);
				pg.strokeWeight(1f);
				pg.strokeJoin(P.MITER);
//				drawPoly(pg, vertices, 100);
//				pg.rect(0, 0, 100, 100);
			}
			i++;
			pg.popMatrix();
		}

		pg.popMatrix();
		
		
//		SphereDistortionFilter.instance(p).setAmplitude(0.3f);
//		SphereDistortionFilter.instance(p).setTime(P.sin(radsComplete) * 1.70f);
//		SphereDistortionFilter.instance(p).applyTo(pg);
		
//		RadialRipplesFilter.instance(p).setTime(-0.15f + 0.05f * P.sin(radsComplete));
//		RadialRipplesFilter.instance(p).setAmplitude(-0.2f + 0.2f * P.sin(radsComplete));
//		RadialRipplesFilter.instance(p).setTime(-0.15f + 0.05f * P.sin(radsComplete));
//		RadialRipplesFilter.instance(p).setAmplitude(-0.2f);
//		RadialRipplesFilter.instance(p).applyTo(pg);
//
//		CubicLensDistortionFilter.instance(p).setTime(-1.0f + 1f * P.sin(radsComplete));
//		CubicLensDistortionFilter.instance(p).applyTo(pg);
		
		pg.endDraw();
	}
	
	protected void drawPoly(PGraphics pg, float vertices, float radius) {
		float segmentRads = P.TWO_PI / vertices;
		pg.beginShape();
		for (int i = 0; i <= vertices; i++) {
			pg.vertex(
					P.sin(segmentRads * (i % vertices)) * radius, 
					P.cos(segmentRads * (i % vertices)) * radius
					);
		}
		pg.endShape();
	}
}
