package me.zakeer.justchat.qb;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import me.zakeer.justchat.R;
import com.quickblox.module.videochat.core.service.QBVideoChatService;
import com.quickblox.module.videochat.core.service.ServiceInteractor;
import com.quickblox.module.videochat.model.listeners.OnCameraViewListener;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.CallType;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.quickblox.module.videochat.views.CameraView;
import com.quickblox.module.videochat.views.OpponentView;

public class ActivityVideoChat extends Activity {

	private CameraView cameraView;
	private OpponentView opponentSurfaceView;
	private ProgressBar opponentImageLoadingPb;
	private VideoChatConfig videoChatConfig;
	private String callType;
	private String callType1;
	Button endCall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_chat_layout);

		endCall=(Button) findViewById(R.id.endcall);
		endCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		// Setup UI
		//
		opponentSurfaceView = (OpponentView) findViewById(R.id.opponentSurfaceView);
		cameraView = (CameraView) findViewById(R.id.camera_preview);
		opponentImageLoadingPb = (ProgressBar) findViewById(R.id.opponentImageLoading);

		// VideoChat settings
		videoChatConfig = getIntent().getParcelableExtra(
				VideoChatConfig.class.getCanonicalName());

		String a = getIntent().getStringExtra("callType");
		
		HashMap<String, String> extraParams = videoChatConfig.getExtraParams();

		callType = getIntent().getParcelableExtra("callType");
		callType1 = extraParams.get("callType");

		Toast.makeText(getApplicationContext(), videoChatConfig.getCallType().toString(), Toast.LENGTH_LONG)
				.show();
		if (videoChatConfig.getCallType() == CallType.VIDEO_AUDIO) {
			
			opponentSurfaceView.setVisibility(0);
			cameraView.setVisibility(0);
			opponentImageLoadingPb.setVisibility(0);
		}
		QBVideoChatService.getService().startVideoChat(videoChatConfig);
	}

	@Override
	public void onResume() {
		QBVideoChatService.getService().setQbVideoChatListener(
				qbVideoChatListener);
		cameraView.setQBVideoChatListener(qbVideoChatListener);
		cameraView.setOnCameraViewListener(new OnCameraViewListener() {
			@Override
			public void onCameraInit(List<Camera.Size> cameraPreviewSizes,
					List<Integer> listFps) {
				cameraView.setCameraPreviewSizeImageQualityCameraFps(
						findMinimalSize(cameraPreviewSizes), 100,
						findMinimalFPS(listFps));
			}
		});
		super.onResume();
	}

	private int findMinimalFPS(List<Integer> fpSs) {
		return (fpSs.get(fpSs.size() - 1) > fpSs.get(0)) ? fpSs.get(0) : fpSs
				.get(fpSs.size() - 1);
	}

	private Camera.Size findMinimalSize(List<Camera.Size> previewSizes) {
		return (previewSizes.get(previewSizes.size() - 1).width > previewSizes
				.get(0).width) ? previewSizes.get(0) : previewSizes
				.get(previewSizes.size() - 1);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		try {
			QBVideoChatService.getService().finishVideoChat(
					videoChatConfig.getSessionId());
		} catch (Exception e) {
			Log.e("", e.toString());
		}
		super.onDestroy();
	}

	OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
		@Override
		public void onCameraDataReceive(byte[] videoData, byte value) {
			if (videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
				return;
			}
			ServiceInteractor.INSTANCE.sendVideoData(ActivityVideoChat.this,
					videoData, value);
		}

		@Override
		public void onMicrophoneDataReceive(byte[] audioData) {
			ServiceInteractor.INSTANCE.sendAudioData(ActivityVideoChat.this,
					audioData);
		}

		@Override
		public void onOpponentVideoDataReceive(byte[] videoData) {
			opponentSurfaceView.setData(videoData);
		}

		@Override
		public void onOpponentAudioDataReceive(byte[] audioData) {
			QBVideoChatService.getService().playAudio(audioData);
		}

		@Override
		public void onProgress(boolean progress) {
			opponentImageLoadingPb.setVisibility(progress ? View.VISIBLE
					: View.GONE);
		}

		@Override
		public void onVideoChatStateChange(CallState callState,
				VideoChatConfig chat) {
			switch (callState) {
			case ON_CALL_START:
				Toast.makeText(getBaseContext(),
						getString(R.string.call_start_txt), Toast.LENGTH_SHORT)
						.show();
				break;
			case ON_CANCELED_CALL:
				Toast.makeText(getBaseContext(),
						getString(R.string.call_canceled_txt),
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			case ON_CALL_END:
				finish();
				break;
			default:
				break;
			}
		}
	};
}
