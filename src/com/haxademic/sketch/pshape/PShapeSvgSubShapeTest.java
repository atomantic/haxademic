package com.haxademic.sketch.pshape;

import processing.core.PShape;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.util.DrawUtil;
import com.haxademic.core.draw.util.OpenGLUtil;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.FileUtil;

@SuppressWarnings("serial")
public class PShapeSvgSubShapeTest 
extends PAppletHax {

	protected PShape _squirrel;
	
	protected PShape _flameLarge;
	protected boolean _flameIsLarge = false;
	protected int _flameFrameSwap = 4;
	
	protected PShape _beamTube;
	protected boolean _beamTubeIsDown = false;
	protected EasingFloat _tubeY = new EasingFloat(0, 3f);
	protected int TUBE_MOVE_HEIGHT = 7;

	protected PShape _faceDefault;
	protected PShape _faceBeaming;
	protected int FACE_MOVE_HEIGHT = 50;
	
	protected PShape _beam;
	protected int BEAM_MOVE_HEIGHT = 40;
	protected EasingFloat _beamY = new EasingFloat(0, 3f);

	protected void overridePropsFile() {
		_appConfig.setProperty( "rendering", "false" );
	}


	public void setup() {
		super.setup();	
//		OpenGLUtil.setQuality( p, OpenGLUtil.SMOOTH_HIGH );
		
		_squirrel = p.loadShape( FileUtil.getHaxademicDataPath() + "svg/squirrel-test-01.svg");
		
		_flameLarge = _squirrel.getChild("flame-large");
		
		_beamTube = _squirrel.getChild("beam-port");
		_beamTube.translate(0, 0);

		_faceDefault = _squirrel.getChild("face-default");
		_faceBeaming = _squirrel.getChild("face-beam");
		_faceBeaming.translate(0, FACE_MOVE_HEIGHT);

		_beam = _squirrel.getChild("beam");
		_beam.translate(0, 0);
	}
	
	public void removeShapeChild( PShape parent, PShape child ) {
		for( int i = 0; i < parent.getChildCount(); i++ ) {
		// for( PShape child: parent.getChildren() ) {
			if( parent.getChild(i) == child ) {
				parent.removeChild(i);
			}
		}
	}

	public void drawApp() {
		background(255);
		DrawUtil.setDrawCenter(p);
		p.translate(p.width/2f, p.height/2f);
		
				
		// animate flame
		if( p.frameCount % _flameFrameSwap == 0 ) {
			_flameIsLarge = !_flameIsLarge;
			if( _flameIsLarge ) {
				_flameLarge.translate(100, 0);
			} else {
				_flameLarge.translate(-100, 0);
			}
		}
		
		// handle drop tube
		_tubeY.update();
		_beamTube.translate(0, _tubeY.value());
		
		_beamY.update();
		_beam.translate(0, _beamY.value());

		if( p.frameCount % 50 == 0 ) {
			_beamTubeIsDown = !_beamTubeIsDown;
			if( _beamTubeIsDown ) {
				tubeDown();
			} else {
				tubeUp();
			}
		}
		
		// draw squirrel
		p.shape(_squirrel);
	}

	protected void tubeDown() {
		_tubeY.setCurrent(TUBE_MOVE_HEIGHT);
		_tubeY.setTarget(0);
		
		_beamY.setCurrent(BEAM_MOVE_HEIGHT);
		_beamY.setTarget(0);
		
		_faceDefault.translate(0, FACE_MOVE_HEIGHT);
		_faceBeaming.translate(0, -FACE_MOVE_HEIGHT);

		
	}
	protected void tubeUp() {
		_tubeY.setCurrent(-TUBE_MOVE_HEIGHT);
		_tubeY.setTarget(0);

		_beamY.setCurrent(-BEAM_MOVE_HEIGHT);
		_beamY.setTarget(0);

		_faceDefault.translate(0, -FACE_MOVE_HEIGHT);
		_faceBeaming.translate(0, FACE_MOVE_HEIGHT);
	}
}