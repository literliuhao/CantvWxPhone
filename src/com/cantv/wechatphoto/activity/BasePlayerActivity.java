package com.cantv.wechatphoto.activity;

import com.cantv.wechatphoto.push.domainvideo.IVideo;


public abstract class BasePlayerActivity<T extends IVideo> {

	public static final String KEY_MOVIE_DETAIL = "moviedetail";
	public static final String KEY_SPECIFIED_PLAY_INDEX = "specifiedPlayIndex";
	public static final String KEY_FREE_PLAY_EPISODE = "freePlayEpisode";
	public static final String KEY_FREE_PLAY_TIME = "freePlayTime";
	public static final String KEY_CHARGE_TYPE = "chargeType";
	public static final String KEY_PLAY_TYPE = "playType";// 取值为PlayConstants中"PLAY_TYPE"，其他非法
	public static final String KEY_PROGRAM_ID = "programId";
	public static final String KEY_VIDEO = "videoInfo";// 进播放器默认播放的剧集数据
	public static final String KEY_BUNDLE = "videobundle";
	
	public static final String ACTION_CLOSE_ONSHOW_PLAYER = "cn.cibntv.ott.action.CLOSE_PLAYER";

	private final int MSG_UPDATE_NET_SPEED = 0x001;
	private final int MSG_CHECK_LOADING = 0x002;
	private final int MSG_CHECK_FREE_PLAY_TIME = 0x003;

	private final int INTERVAL_UPDATE_NET_SPEED = 1000;
	private final int INTERVAL_CHECK_LOADING = 500;
	private final int INTERVAL_CHECK_FREE_PLAY_TIME = 1000;
	private final int DEFAULT_CONTROLLER_SHOW_TIME = 5000;

	protected final int CONFIG_NO_CHANGED = 0x10000;

	/*protected IPlayerControl mVideoPlayer;
	protected IPlayerUI mUIControl;
	protected FilterDialogView mConfigDialog;
	private Dialog mExitDialog;
	private RecDialogView mPlayHistoryDialog;

	protected ProgramInfo<T> mProgramInfo;
	private PlayRecordHelpler mPlayerRecoder;
	private Handler mHandler = null;
	private String mNetSpeedTemplate;
	private BroadcastReceiver mClosePageReceiver;
	private IntentFilter mClosePageIntentFilter;

	protected int mSpecifiedPlayIndex;
	protected String mChargeType;
	protected int mCurrPlayIndex;
	protected int mPlayType;
	private int historyPlayProgress = 0;
	private boolean hasRecordComplete = false;
	private boolean firstShowProgressDialog = true;
	protected boolean isNetConnected = true;
	private boolean keepPlayerPauseStatus = false;
	private boolean isPlayerPrepared = false;
	private boolean isPlayerCompletion = false;

	protected long mBeginTime = 0;
	protected long mEndTime = 0;
	protected long mPlayTime = 0;
	protected long mDuration = 0;

	*//**
	 * 播放影片时指定起始位置 默认：0
	 *//*
	private int destPosition = 0;

	*//**
	 * 免费播放集数 <br>
	 * default：-1 <br>
	 *//*
	protected int mFreePlayEpisode = -1;//
	*//**
	 * 免费播放时长 <br>
	 * default：-1 <br>
	 *//*
	protected int mFreePlayTime = -1;
	*//**
	 * 指播放器自身支持的清晰度类型值, <br>
	 * ps: 并非"PlayerConstants"中定义的清晰度
	 *//*
	protected int mCurrDefinition;
	*//**
	 * 指播放器自身支持的视频宽高比 <br>
	 * ps: 并非"PlayerConstants"中定义的视频宽高比
	 *//*
	protected int mCurrScreenRatio;
	// ConfigDialog中数据
	private List<Integer> mSupportDefinitions = null;
	private List<String> mSupportDefinitionValues = null;
	private List<Integer> mScreenRatios = null;
	private List<String> mScreenRatioValues = null;
	// 剧集列表
	private List<String> mEpisodes = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setFormat(android.graphics.PixelFormat.TRANSPARENT);
		initData();// 初始化全局数据
		initUI();// 初始化布局
		initPlayer();// 初始化播放器
		initController();// 初始化播控UI
		initHandler();
		prepareProgramData();
		regAutoClosePageBroadcast();
	}

	private void regAutoClosePageBroadcast() {
		if (mClosePageReceiver == null) {
			mClosePageReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					Log.i(TAG, "received close page broadcast, auto closed page...");
					finish();
				}
			};
			mClosePageIntentFilter = new IntentFilter(ACTION_CLOSE_ONSHOW_PLAYER);
		}
		registerReceiver(mClosePageReceiver, mClosePageIntentFilter);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent");
		setIntent(intent);
		initData();
		resetUI();
		prepareProgramData();
	}

	*//**
	 * 初始化参数<br>
	 * 如果需要 子类可复写此方法
	 *//*
	protected void initData() {
		Intent intent = getIntent();
		mPlayType = intent.getIntExtra(KEY_PLAY_TYPE, PlayConstants.PLAY_TYPE.NORMAL);
		mFreePlayEpisode = intent.getIntExtra(KEY_FREE_PLAY_EPISODE, -1);
		mFreePlayTime = intent.getIntExtra(KEY_FREE_PLAY_TIME, -1);
		mChargeType = intent.getStringExtra(KEY_CHARGE_TYPE);
		mSpecifiedPlayIndex = intent.getIntExtra(KEY_SPECIFIED_PLAY_INDEX, 0);
		if (mSpecifiedPlayIndex < 0) {
			Log.i(TAG, "Illegal specifiedPlayIndex : " + mSpecifiedPlayIndex + ", auto asign to 0");
			mSpecifiedPlayIndex = 0;
		}
		mCurrPlayIndex = mSpecifiedPlayIndex;
		mCurrDefinition = getDefaultDefinition();
		// 默认全屏播放
		mCurrScreenRatio = convertScreenRatio2Local(PlayConstants.VIDEO_RATIO.FULLSCREEN);
	}

	private void resetUI() {
		if (mUIControl != null && mUIControl instanceof VideoPlayerController) {
			VideoPlayerController controller = (VideoPlayerController) mUIControl;
			controller.hide();
			controller.updateTitle("");
			controller.updateDuration(0);
			controller.updateProgressUI(0);
		}
		if (mVideoPlayer != null) {
			mVideoPlayer.stop();
		}
		hideExitDialog();
		hideConfigDialog();
		hidePlayerErrDialog();
		hidePlayHistoryDialog();
		mExitDialog = null;
		mConfigDialog = null;
		mPlayerErrDialog = null;
		mPlayHistoryDialog = null;
	}

	private void initController() {
		if (mUIControl != null && mUIControl instanceof VideoPlayerController) {
			VideoPlayerController controller = (VideoPlayerController) mUIControl;
			controller.setOnActionListener(new OnActionListener() {

				@Override
				public void playNextEpisode() {
					boolean success = BasePlayerActivity.this.playNextEpisode();
					if (success && mUIControl != null && mUIControl instanceof VideoPlayerController) {
						((VideoPlayerController) mUIControl).hidePauseLayout();
					}
				}

				@Override
				public void seekToNextEpisode() {
					isPlayerCompletion = true;
					BasePlayerActivity.this.playNextEpisode();
				}

				@Override
				public void playLastEpisode() {
					boolean success = BasePlayerActivity.this.playLastEpisode();
					if (success && mUIControl != null && mUIControl instanceof VideoPlayerController) {
						((VideoPlayerController) mUIControl).hidePauseLayout();
					}
				}
			});
		}
		mNetSpeedTemplate = "%d KB/S";
	}

	*//**
	 * 获取总下载流量
	 * 
	 * @return
	 *//*
	private long getTotalRxBytes() {
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		return totalRxBytes == TrafficStats.UNSUPPORTED ? 0 : (totalRxBytes / 1024);
	}

	private void initHandler() {
		mHandler = new SafeHandler(BasePlayerActivity.this) {

			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				Message newMsg = null;
				switch (msg.what) {
				// 更新网速
				case MSG_UPDATE_NET_SPEED:
					long totalBytes = getTotalRxBytes();
					long currTime = SystemClock.elapsedRealtime();
					Bundle data = msg.peekData();
					if (data == null) {
						data = new Bundle();
					} else {
						long rxBytes = totalBytes - data.getLong("totalBytes");
						float elapsedSeconds = (currTime - data.getLong("lastTime")) * 1.0f / 1000;
						int netSpeed = (int) (rxBytes / elapsedSeconds);
						String netSpeedStr = String.format(mNetSpeedTemplate, netSpeed);
						if (mUIControl != null) {
							mUIControl.notifyNetSpeedChanged(netSpeed, netSpeedStr);
						}

					}
					newMsg = obtainMessage(MSG_UPDATE_NET_SPEED);
					data.putLong("totalBytes", totalBytes);
					data.putLong("lastTime", currTime);
					newMsg.setData(data);
					sendMessageDelayed(newMsg, INTERVAL_UPDATE_NET_SPEED);
					break;
				// 检测视频卡顿
				case MSG_CHECK_LOADING:
					newMsg = Message.obtain(msg);
					int latestProgress = msg.arg1;
					int currProgress = 0;
					if (mVideoPlayer != null) {
						currProgress = mVideoPlayer.getCurrentPosition();
						// Log.d(TAG, "progress: " + currProgress + " -- " + mVideoPlayer.getDuration());
						newMsg.arg1 = currProgress;
					}
					if (mVideoPlayer != null) {
						if (latestProgress == currProgress) {
							// 播放器卡顿
							if (isResumed()) {
								if (!isNetConnected) {
									showPlayerErrorDialog(false, 0, 0);
								} else {
									if (mUIControl != null) {
										mUIControl.notifyLoadingStart();
									}
								}
							}
						} else {
							// 播放器恢复播放
							hidePlayerErrDialog();
							if (mUIControl != null) {
								mUIControl.notifyLoadingStop();
							}
						}
					}
					sendMessageDelayed(newMsg, INTERVAL_CHECK_LOADING);
					break;
				// 检测免费播放时长
				case MSG_CHECK_FREE_PLAY_TIME:
					if (mVideoPlayer != null && mFreePlayTime > 0
							&& mVideoPlayer.getCurrentPosition() >= mFreePlayTime) {
						toastFreePlayFailure();
						finish();
					} else {
						sendMessageDelayed(Message.obtain(msg), INTERVAL_CHECK_FREE_PLAY_TIME);
					}
					break;
				default:
					break;
				}
			};
		};
	}

	protected abstract void initUI();

	protected abstract void initPlayer();

	protected abstract void prepareProgramData();

	*//**
	 * 映射 PlayerConstants类中DEFINITION 到 本播放器对应的视频分辨率
	 * 
	 * @param definition
	 * @return
	 *//*
	protected abstract int convertDefinition2Local(int definition);

	*//**
	 * 映射 本播放器对应的视频分辨率 到 PlayerConstants类中DEFINITION
	 * 
	 * @param definition
	 * @return
	 *//*
	protected abstract int convertDefinition2Standard(int definition);

	*//**
	 * 映射 PlayerConstants类中SCREEN_RATIO 到 本播放器对应的视频比例
	 * 
	 * @param definition
	 * @return
	 *//*
	protected abstract int convertScreenRatio2Local(int ratio);

	*//**
	 * 映射 本播放器对应的视频比例 到 映射 PlayerConstants类中SCREEN_RATIO
	 * 
	 * @param definition
	 * @return
	 *//*
	protected abstract int convertScreenRatio2Standard(int ratio);

	*//**
	 * 更新当前剧集支持的清晰度列表
	 * 
	 * @param mCurrPlayIndex
	 * @param definitions
	 *            清晰度类型列表
	 * @param definitionValues
	 *            对应的清晰度文字列表
	 *//*
	protected abstract void updateSupportedDefinitions(int playIndex, List<Integer> definitions,
			List<String> definitionValues);

	*//**
	 * 更新当前播放器支持的视频宽高比
	 * 
	 * @param screenRatios
	 *            视频宽高比列表
	 * @param screenRatioValues
	 *            对应的视频宽高比文字列表
	 *//*
	protected abstract void updateScreenRatios(List<Integer> screenRatios, List<String> screenRatioValues);

	*//**
	 * 改变播放器参数<br>
	 * 注意：所有参数均已适配本播放器，无需再转换<br>
	 * <br>
	 * 如果某项参数无需改变，则对应参数值为 BaseVideoPlayerActivity.CONFIG_NO_CHANGED
	 * 
	 * @param definition
	 *            分辨率
	 * @param screenRatio
	 *            视频比比例
	 * @param episodeIndex
	 *            播放剧集
	 * 
	 *//*
	protected abstract void changePlayerConfig(int definition, int screenRatio, int episodeIndex);

	*//**
	 * 播放器播放指定影片<br>
	 * 
	 * @param playIndex
	 *            播放第几集（已作正确性校验）
	 * @param video
	 *            播放该集影片所需数据
	 * @param startPosition
	 *            起始播放位置
	 *//*
	protected abstract void playVideo(T video);

	@Override
	protected void onStart() {
		super.onStart();
		if (mFreePlayTime > 0) {
			startCheckFreePlayTime();
		}
		if (mHandler != null) {
			mHandler.removeMessages(MSG_UPDATE_NET_SPEED);
			mHandler.sendEmptyMessage(MSG_UPDATE_NET_SPEED);
			mHandler.removeMessages(MSG_CHECK_LOADING);
			mHandler.sendEmptyMessage(MSG_CHECK_LOADING);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// 切换信号源后,继续上次进度播放,并保持原有暂停与否的状态
		notifyKeepPlayerPauseStatus();
		playVideo(mCurrPlayIndex, historyPlayProgress);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mVideoPlayer != null && mUIControl != null && mUIControl instanceof VideoPlayerController
				&& !((VideoPlayerController) mUIControl).isPausedUIVisible()) {
			mVideoPlayer.start();
		}
		mBeginTime = System.currentTimeMillis();
		MobclickAgent.onResume(getApplicationContext());
	}

	@Override
	protected void onPause() {
		if (mVideoPlayer != null) {
			mVideoPlayer.pause();
			historyPlayProgress = mVideoPlayer.getCurrentPosition();
		}
		recordPlayProgress();
		mEndTime = System.currentTimeMillis();
		mPlayTime += (mEndTime - mBeginTime) / 1000;
		MobclickAgent.onPause(getApplicationContext());
		super.onPause();
	}

	@Override
	protected void onStop() {
		// 统计点播时长/次数
		videoCollector();
		if (mFreePlayTime > 0) {
			stopCheckFreePlayTime();
		}
		if (mVideoPlayer != null) {
			mVideoPlayer.stop();
		}
		if (mHandler != null) {
			mHandler.removeMessages(MSG_UPDATE_NET_SPEED);
			mHandler.removeMessages(MSG_CHECK_LOADING);
		}
		super.onStop();
	}

	private void videoCollector() {
		VideoPlayerController videoController = (VideoPlayerController) mUIControl;
		try {
			mDuration = mPlayTime - videoController.mTotalPauseTime - mTotalErrorTime;
		} catch (Exception e) {
			mDuration = mPlayTime;
		}
		HashMap<String, String> durationMap = new HashMap<String, String>();
		durationMap.put("videoType", mProgramInfo.getType());
		durationMap.put("videoName", mProgramInfo.getName());
		durationMap.put("duration", String.valueOf(mDuration));
		CollectorAgent.onEvent("1", durationMap);
		resetVariable(videoController);
	}

	private void resetVariable(VideoPlayerController videoController) {
		mDuration = 0;
		mPlayTime = 0;
		mStartErrorTime = 0;
		mEndErrorTime = 0;
		mTotalErrorTime = 0;
		try {
			videoController.mTotalPauseTime = 0;
			videoController.mStartPasueTime = 0;
			videoController.mEndPauseTime = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		if (mClosePageReceiver != null) {
			unregisterReceiver(mClosePageReceiver);
		}
		hidePlayerErrDialog();
		hideConfigDialog();
		hideExitDialog();
		hidePlayHistoryDialog();
		if (mPlayerRecoder != null) {
			mPlayerRecoder = null;
		}
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
			mHandler = null;
		}
		if (mVideoPlayer != null) {
			mVideoPlayer.destroy();
			mVideoPlayer = null;
		}
		if (mUIControl != null) {
			mUIControl.release();
			mUIControl = null;
		}
		super.onDestroy();
	}

	*//**
	 * 检测免费播放时长
	 *//*
	final protected void startCheckFreePlayTime() {
		if (mHandler != null) {
			mHandler.removeMessages(MSG_CHECK_FREE_PLAY_TIME);
			mHandler.sendEmptyMessage(MSG_CHECK_FREE_PLAY_TIME);
		}
	}

	final protected void stopCheckFreePlayTime() {
		if (mHandler != null) {
			mHandler.removeMessages(MSG_CHECK_FREE_PLAY_TIME);
		}
	}

	*//**
	 * 是否有上一集
	 * 
	 * @param index
	 * @return
	 *//*
	final protected boolean hasLastEpisode(int index) {
		if (mProgramInfo == null) {
			return false;
		}
		return index > 0 && index < mProgramInfo.getVideoCount();
	}

	*//**
	 * 是否有下一集
	 * 
	 * @param index
	 * @return
	 *//*
	final protected boolean hasNextEpisode(int index) {
		if (mProgramInfo == null) {
			return false;
		}
		if (mFreePlayEpisode > 0) {
			return index >= 0 && index < mFreePlayEpisode - 1;
		}
		return index >= 0 && index < mProgramInfo.getVideoCount() - 1;
	}

	*//**
	 * 上一集
	 *//*
	final protected boolean playLastEpisode() {
		return playVideo(mCurrPlayIndex-1, 0);
	}

	*//**
	 * 下一集
	 *//*
	final protected boolean playNextEpisode() {
		return playVideo(mCurrPlayIndex+1, 0);
	}

	*//**
	 * 播放指定剧集
	 * 
	 * @param index
	 *            剧集
	 *//*
	final protected boolean playVideo(int index) {
		return playVideo(index, 0);
	}

	*//**
	 * 播放指定集数
	 * 
	 * @param index
	 *            剧集
	 * @param startPosition
	 *            起始播放位置
	 *//*
	final protected boolean playVideo(int index, int startPosition) {
				
		if (mVideoPlayer == null) {
			return false;
		}

		if (mProgramInfo == null) {
			Log.i(TAG, "mProgramInfo == NULL, cancel invoke method playIndex().");
			toastDataSourceError();
			return false;
		}

		int videoCount = mProgramInfo.getVideoCount();
		if (videoCount == 0) {
			Log.w(TAG, "playIndex(" + index + ") failed. [Empty videoSource list]");
			toastDataSourceError();
			return false;
		}

		boolean indexOutOfLowerBounds = index < 0;
		if (indexOutOfLowerBounds) {
			return false;
		}

		boolean isFreePlayEpisodeLimitted = mFreePlayEpisode > 0;
		if (!isFreePlayEpisodeLimitted) { // 免费
			int maxIndex = videoCount - 1;
			if (index > maxIndex) { // 没有下一集
				if (mPlayType != PlayConstants.PLAY_TYPE.PUSH) { // 非推送节目
					recordPlayComplete();
				}
				if (isPlayerCompletion) {
					finish();
				}
				return false;
			}
		} else { // 收费
			int maxIndex = mFreePlayEpisode - 1;
			if (index > maxIndex) {
				if (mPlayType != PlayConstants.PLAY_TYPE.PUSH) { // 非推送节目
					toastFreePlayFailure();
					if (isPlayerCompletion) {// 节目是否已播放完毕
						recordPlayComplete();// 收费节目播放记录不记录进度
						finish();
					}
				} else {
					toastFreePlayEpisodeError();
					if (isPlayerCompletion) {// 节目是否已播放完毕
						finish();
					}
				}
				return false;
			}
		}
		mCurrPlayIndex = index;
		updateNextPreviousUI(index);
		if (mUIControl != null && mUIControl instanceof VideoPlayerController) {
			VideoPlayerController controller = (VideoPlayerController) mUIControl;
			if (!keepPlayerPauseStatus) {
				controller.hidePauseLayout();
				controller.showLoadingView();
			} else {
				controller.showLoadingIfNoPause();
			}
			keepPlayerPauseStatus = false;
			controller.hideBottomLayout();
			controller.updateDuration(0);
			controller.updateProgressUI(0);
			controller.show(DEFAULT_CONTROLLER_SHOW_TIME);
		}
		playVideo(mProgramInfo.getVideo(index), startPosition);
		isPlayerCompletion = false;
		return true;
	}

	protected void updateNextPreviousUI(int playIndex) {
		boolean hasLastEpisode = hasLastEpisode(playIndex);
		boolean hasNextEpisode = hasNextEpisode(playIndex);
		if (mUIControl != null && mUIControl instanceof VideoPlayerController) {
			VideoPlayerController controller = (VideoPlayerController) mUIControl;
			T video = mProgramInfo.getVideo(playIndex);
			if (video != null) {
				controller.updateTitle(video.getTitle());
			}
			controller.updatePauseLayout(hasLastEpisode, hasNextEpisode);
		}
		if (mExitDialog != null && mExitDialog instanceof ExitDialogView) {
			((ExitDialogView) mExitDialog).updateBtnVisible(hasLastEpisode, hasNextEpisode);
		}
	}

	*//**
	 * 播放单片
	 * 
	 * @param video
	 *            剧集对应单片数据
	 * @param startPosition
	 *            起始播放位置
	 *//*
	protected void playVideo(T video, int startPosition) {
		if (video == null) {
			toastDataSourceError();
			return;
		}
		destPosition = startPosition;
		playVideo(video);
	}

	*//**
	 * 显示视频参数设置/剧集选择对话框
	 *//*
	final protected void showConfigDialog() {
		if (mProgramInfo == null) {
			return;
		}
		if (mConfigDialog == null) {
			List<String> supportedDefinitions = getSupportedDefinitions();
			if (supportedDefinitions == null || supportedDefinitions.isEmpty()) {
				Log.w(TAG, "Failed to showConfigDialog, waiting for loading definitionList.");
				return;
			}
			List<String> videoRatios = getVideoRatioList();
			if (videoRatios == null || videoRatios.isEmpty()) {
				Log.w(TAG, "Failed to showConfigDialog, waiting for loading videoRatioList.");
				return;
			}
			List<String> episodeList = generateEpisodeList();
			if (episodeList == null || episodeList.isEmpty()) {
				Log.w(TAG, "Failed to showConfigDialog, waiting for loading episodeList.");
				return;
			}
			mConfigDialog = new FilterDialogView(this, R.style.PlayDialogStyle);
			mConfigDialog.setProgramSeriesId(mProgramInfo.getId());
			mConfigDialog.addDistinList(supportedDefinitions);
			mConfigDialog.addRatioList(videoRatios);
			mConfigDialog.addDramaList(episodeList);
			boolean isPushPlay = mPlayType == PlayConstants.PLAY_TYPE.PUSH;
			mConfigDialog.addFavView(!isPushPlay, false);
			mConfigDialog.setOnclickLinstener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mConfigDialog.dismiss();
					if (v.equals(mConfigDialog.getDistinlayout()) || v.equals(mConfigDialog.getRatiolayout())
							|| v.equals(mConfigDialog.getDramalayout())) {
						int definitionIndex = mConfigDialog.getIdisindex();
						int screenRatioIndex = mConfigDialog.getIredioindex();
						int episodeIndex = mConfigDialog.getIdramaindexe();
						int newDefinition = mSupportDefinitions.get(definitionIndex);
						int newScreenRatio = mScreenRatios.get(screenRatioIndex);
						boolean definitionChanged = mCurrDefinition != newDefinition;
						boolean VideoRatioChanged = mCurrScreenRatio != newScreenRatio;
						boolean playIndexChanged = mCurrPlayIndex != episodeIndex;
						if (playIndexChanged && mFreePlayEpisode > 0 && episodeIndex > mFreePlayEpisode - 1) {
							if (mPlayType == PlayConstants.PLAY_TYPE.PUSH) {
								toastFreePlayEpisodeError();
							} else {
								toastFreePlayFailure();
							}
							return;
						}
						changePlayerConfig(definitionChanged ? newDefinition : CONFIG_NO_CHANGED,
								VideoRatioChanged ? newScreenRatio : CONFIG_NO_CHANGED,
								playIndexChanged ? episodeIndex : CONFIG_NO_CHANGED);
					}
				}
			});
		} else {
			*//***** TODO 不建议这么做，用adapter.notifyDataSetChanged即可, 暂时先保留，以后再改 by zby ****//*
			mConfigDialog.getDistinlstview()
					.setAdapter(new FilterListAdapter(BasePlayerActivity.this, getSupportedDefinitions(), ""));
			*//*********//*
		}
		mConfigDialog.setIMove1(getCurrDefinitionIndex());
		mConfigDialog.setIMove2(getCurrVideoRatioIndex());
		mConfigDialog.setIMove3(mCurrPlayIndex);
		mConfigDialog.showDialog();
	}

	*//**
	 * 更新剧集选择对话框中剧集列表
	 *//*
	protected void refreshEpisodeList() {
		if (mConfigDialog == null) {
			return;
		}
		List<String> EpisodeList = generateEpisodeList();
		mConfigDialog.updateDramaList(EpisodeList);
		mConfigDialog.setIMove3(mCurrPlayIndex);
	}

	*//**
	 * 获取视频分辨率列表
	 * 
	 * @return
	 *//*
	private List<String> getSupportedDefinitions() {
		if (mSupportDefinitions == null) {
			mSupportDefinitions = new ArrayList<Integer>();
			mSupportDefinitionValues = new ArrayList<String>();
		} else {
			mSupportDefinitions.clear();
			mSupportDefinitionValues.clear();
		}
		updateSupportedDefinitions(mCurrPlayIndex, mSupportDefinitions, mSupportDefinitionValues);
		if (mSupportDefinitions.size() == 0 || mSupportDefinitionValues.size() == 0) {
			Log.e(TAG, "Error in updating supportedDefinitions.[Empty definition list]");
		}
		if (mSupportDefinitions.size() != mSupportDefinitionValues.size()) {
			Log.e(TAG, "Error in updating supportedDefinitions.[Unmatched definition list]");
		}
		return mSupportDefinitionValues;
	}

	*//**
	 * 获取视频比例列表
	 * 
	 * @return
	 *//*
	private List<String> getVideoRatioList() {
		if (mScreenRatios == null) {
			mScreenRatios = new ArrayList<Integer>();
			mScreenRatioValues = new ArrayList<String>();
			updateScreenRatios(mScreenRatios, mScreenRatioValues);
			if (mScreenRatios.size() != mScreenRatioValues.size()) {
				Log.e(TAG, "Error in updating supportedScreenRatios.[Unmatched list]");
			}
		}
		return mScreenRatioValues;
	}

	*//**
	 * 隐藏视频参数设置对话框
	 *//*
	final protected void hideConfigDialog() {
		if (mConfigDialog != null) {
			mConfigDialog.dismiss();
		}
	}

	private int getCurrDefinitionIndex() {
		int currDefinitionIndex = 0;
		for (int i = 0, count = mSupportDefinitions.size(); i < count; i++) {
			if (mCurrDefinition == mSupportDefinitions.get(i)) {
				currDefinitionIndex = i;
				break;
			}
		}
		return currDefinitionIndex;
	}

	*//**
	 * 计算"当前视频比例"在视频比例列表中的位置
	 * 
	 * @return
	 *//*
	private int getCurrVideoRatioIndex() {
		int currVideoRatioIndex = 0;
		for (int i = 0, count = mScreenRatios.size(); i < count; i++) {
			if (mCurrScreenRatio == mScreenRatios.get(i)) {
				currVideoRatioIndex = i;
				break;
			}
		}
		return currVideoRatioIndex;
	}

	*//**
	 * 生成剧集列表
	 * 
	 * @return
	 *//*
	private List<String> generateEpisodeList() {
		if (mEpisodes == null) {
			mEpisodes = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			List<T> videos = mProgramInfo.getVideoList();
			int episodeCount = videos.size();
			for (int i = 0; i < episodeCount; i++) {
				sb.setLength(0);
				mEpisodes.add(sb.append("第").append(videos.get(i).getEpisode()).append("集").toString());
			}
		}
		return mEpisodes;
	}

	*//**
	 * 获取设置页面指定的prefer清晰度
	 * 
	 * @return
	 *//*
	final protected int getDefaultDefinition() {
		int definition = PlayConstants.DEFINITION.UHD;
		String defaultDefinition = LocalData.getInstance(this)
				.getKV(AppGlobalConsts.PERSIST_NAMES.VIDEO_DEFAULT_DEFINITION.name());
		if (TextUtils.isEmpty(defaultDefinition)) {
			// do nothing
		} else if (defaultDefinition.equals("0")) {// 超清
			definition = PlayConstants.DEFINITION.UHD;
		} else if (defaultDefinition.equals("1")) {// 高清
			definition = PlayConstants.DEFINITION.HD;
		} else if (defaultDefinition.equals("2")) {// 标清
			definition = PlayConstants.DEFINITION.SD;
		}
		return convertDefinition2Local(definition);
	}

	*//**
	 * 显示历史播放进度提示框
	 * 
	 * @param timeMillis
	 *//*
	final protected void showPlayHistoryDialog(int timeMillis) {
		// 显示此进度对话框的最小时间单位是second，小于一秒钟的进度时间忽略
		if (timeMillis <= 1000) {
			return;
		}
		mPlayHistoryDialog = new RecDialogView(this, R.style.PlayDialogStyle, "00:00:00");
		mPlayHistoryDialog.addRecView();
		mPlayHistoryDialog.setBreakTime(timeMillis);
		mPlayHistoryDialog.setOkButtonOnclick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mVideoPlayer.seekTo(0);
				mPlayHistoryDialog.recDialogHide();
			}
		});
		mPlayHistoryDialog.setCancelButtonOnclick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPlayHistoryDialog.recDialogHide();
			}
		});
		mPlayHistoryDialog.recDialogShow();
	}

	final protected void hidePlayHistoryDialog() {
		if (mPlayHistoryDialog != null) {
			mPlayHistoryDialog.dismiss();
		}
	}

	final protected void toggleExitDialog() {
		if (mExitDialog != null && mExitDialog.isShowing()) {
			hideExitDialog();
		} else {
			showExitDialog();
		}
	}

	*//**
	 * 显示退出对话框
	 *//*
	final protected void showExitDialog() {
		if (mPlayType == PlayConstants.PLAY_TYPE.PUSH) {
			if (mExitDialog == null) {
				mExitDialog = PromptUtils.showCommonDialog(this, getString(R.string.exit_player_tips),
						getString(R.string.confirm), getString(R.string.cancel), new OnBtnClickListener() {

							@Override
							public void onClickPositive() {
								finish();
							}

							@Override
							public void onClickNegative() {
							}
						});
			} else {
				mExitDialog.show();
			}
		} else {
			if (mExitDialog == null) {
				String programId = (mProgramInfo == null || TextUtils.isEmpty(mProgramInfo.getId())) ? ""
						: mProgramInfo.getId();
				final ExitDialogView exitDialog = new ExitDialogView(BasePlayerActivity.this, R.style.PlayDialogStyle,
						programId, mProgramInfo.getName());
				exitDialog.setOnItemClick(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						exitDialog.dismiss();
						// Goto ProgramDetail page
						Intent intent = new Intent(BasePlayerActivity.this, EntryPoint.class);
						Bundle op = new Bundle();
						op.putString("programSeriesId", exitDialog.getProlist().get(position).getId());
						intent.putExtra(AppGlobalConsts.INTENT_ACTION_NAME, BasePage.ACTION_NAME.OPEN_DETAIL.name());
						intent.putExtras(op);
						startActivity(intent);
						finish();
					}
				});
				exitDialog.setLastBtnOnclick(new OnClickListener() {

					@Override
					public void onClick(View v) {
						exitDialog.dismiss();
						playLastEpisode();
					}
				});
				exitDialog.setNextBtnOnclick(new OnClickListener() {

					@Override
					public void onClick(View v) {
						exitDialog.dismiss();
						playNextEpisode();
					}
				});
				exitDialog.setExitBtnOnclick(new OnClickListener() {

					@Override
					public void onClick(View v) {
						exitDialog.dismiss();
						finish();
					}
				});
				exitDialog.setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
							exitDialog.dismiss();
							return true;
						}
						return false;
					}
				});
				mExitDialog = exitDialog;
			}
			((ExitDialogView) mExitDialog).updateBtnVisible(hasLastEpisode(mCurrPlayIndex),
					hasNextEpisode(mCurrPlayIndex));
			((ExitDialogView) mExitDialog).showDialog();
		}
	}

	final protected void hideExitDialog() {
		if (mExitDialog != null) {
			mExitDialog.dismiss();
		}
	}

	*//**
	 * 记录当前播放进度
	 *//*
	final protected void recordPlayProgress() {
		if (mVideoPlayer == null || hasRecordComplete || mProgramInfo == null || mCurrPlayIndex < 0
				|| mCurrPlayIndex >= mProgramInfo.getVideoCount()) {
			return;
		}
		recordPlayProgress(mCurrPlayIndex, mVideoPlayer.getCurrentPosition(), mVideoPlayer.getDuration());
	}

	*//**
	 * 记录所有剧集已看完，播放进度重置
	 *//*
	final protected void recordPlayComplete() {
		Log.i(TAG, "recordPlayComplete");// 勿删
		recordPlayProgress(0, 0, 0);
		hasRecordComplete = true;
	}

	final protected void recordPlayProgress(final int playIndex, final int progress, final int duration) {
		Log.i(TAG, "recordPlayProgress playIndex = " + playIndex + ", progress = " + progress + ", duration = "
				+ duration);// 勿删
		if (mPlayType == PlayConstants.PLAY_TYPE.PUSH) {
			// 推送观看的影片，本地不记录
			Log.i(TAG, "Cancel recordPlayProgress [playType == PlayConstants.PLAY_TYPE.PUSH].");
			return;
		}
		if (hasRecordComplete) {
			// 节目播放完成，已调用recordPlayComplete重置播放记录，无需再重复记录
			Log.i(TAG, "Cancel to recordPlayProgress [has record play complete].");
			return;
		}
		if (!isPlayerPrepared) {
			// 进入播放器后，节目未缓冲成功，无需保存播放记录
			Log.i(TAG, "Cancel to recordPlayProgress [mediaPlayer didn't prepared].");
			return;
		}
		if (mProgramInfo == null) {
			Log.i(TAG, "Failed to recordPlayProgress [mProgramInfo == NULL].");
			return;
		}
		MPlayRecordInfo recordInfo = new MPlayRecordInfo();
		recordInfo.setEpgId(mProgramInfo.getId());
		recordInfo.setType(mProgramInfo.getType());
		recordInfo.setPlayerName(mProgramInfo.getName());
		recordInfo.setUserId(App.getCurrUserId());
		recordInfo.setPriceIcon(mProgramInfo.getChargeType());
		recordInfo.setPlayerpos(playIndex);
		recordInfo.setPonitime((mFreePlayTime > 0 || mFreePlayEpisode > 0) ? 0 : progress);
		recordInfo.setTotalTime(duration);
		recordInfo.setPicUrl(mProgramInfo.getPosterUrl());
		recordInfo.setDateTime(System.currentTimeMillis());
		recordInfo.setIsSingle(mProgramInfo.getVideoCount() == 1 ? 1 : 0);
		T video = mProgramInfo.getVideo(playIndex);
		if (video != null) {
			recordInfo.setDetailsId(video.getId());
		}
		if (mPlayerRecoder == null) {
			mPlayerRecoder = new PlayRecordHelpler(getApplicationContext());
		}
		mPlayerRecoder.savePlayRecord(recordInfo);
	}

	*//**
	 * 获取对应某集播放进度
	 * 
	 * @param playIndex
	 * @return
	 *//*
	final protected int queryPlayProgress(int playIndex) {
		if (mPlayerRecoder == null) {
			mPlayerRecoder = new PlayRecordHelpler(getApplicationContext());
		}
		MPlayRecordInfo recordInfo = mPlayerRecoder.findPlayRecordByEpgId(App.getCurrUserId(), mProgramInfo.getId());
		if (recordInfo == null) {
			return 0;
		}

		int historyPlayIndex = recordInfo.getPlayerpos();
		if (playIndex != historyPlayIndex) {
			return 0;
		}
		return recordInfo.getPonitime();
	}

	final protected void toastFreePlayFailure() {
		if (mPlayType == PlayConstants.PLAY_TYPE.PUSH) {
			ToastUtils.showMessage(getApplicationContext(), getString(R.string.pushPlayFreeTimeoutTips),
					Toast.LENGTH_LONG);
		} else{
			if (!App.isLogin()) {
				ToastUtils.showMessage(getApplicationContext(), getString(R.string.please_login), Toast.LENGTH_LONG);
			}else{
				ToastUtils.showMessage(getApplicationContext(), getString(R.string.buy2PlayTips), Toast.LENGTH_LONG);
			}
		}
	}

	final protected void toastFreePlayEpisodeError() {
		String tipStr = getString(R.string.pushPlayFreeEpisodeTips);
		tipStr = String.format(tipStr, mFreePlayEpisode);
		ToastUtils.showMessage(getApplicationContext(), tipStr, Toast.LENGTH_LONG);
	}

	final protected void toastDataSourceError() {
		ToastUtils.showMessage(getApplicationContext(), getString(R.string.player_data_src_err), Toast.LENGTH_LONG);
	}
	
	final protected void toastPushDataError() {
		ToastUtils.showMessage(getApplicationContext(), getString(R.string.data_err_pls_back), Toast.LENGTH_LONG);
	}

	@Override
	protected void onConnectivityChange() {
		isNetConnected = NetUtils._checkNet(App.getContext());
		if (isNetConnected && mVideoPlayer != null
				&& ((mUIControl != null && mUIControl instanceof VideoPlayerController
						&& !((VideoPlayerController) mUIControl).isPausedUIVisible()) || !mVideoPlayer.isPaused())) {
			mVideoPlayer.start();
		}
	}

	@Override
	protected void onTimeTick() {
		if (mUIControl != null) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			int miniute = calendar.get(Calendar.MINUTE);
			if (miniute == 0 || miniute == 30) {
				mUIControl.show(DEFAULT_CONTROLLER_SHOW_TIME);
			}
		}
	}

	@Override
	protected void onHomeKeyDown() {
		finish();
	}

	@Override
	public boolean onDispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			Log.d(TAG, "dispatchKeyEvent : " + keyCode);
		}

		// 播广告期间，只响应返回键
		if (mVideoPlayer != null && mVideoPlayer.isPlayingAdvert() && keyCode != KeyEvent.KEYCODE_BACK
				&& keyCode != KeyEvent.KEYCODE_ESCAPE) {
			return true;
		}

		int action = event.getAction();
		boolean isActionDown = action == KeyEvent.ACTION_DOWN;
		boolean isUniqueDown = isActionDown && event.getRepeatCount() == 0;

		if (isUniqueDown) {
			if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
				if (mUIControl != null) {
					mUIControl.dispatchKeyEvent(event);
				}
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
				if (mUIControl != null) {
					mUIControl.dispatchKeyEvent(event);
				}
			}
		}

		// controller处理相关key事件
		if (mUIControl != null && mUIControl.dispatchKeyEvent(event)) {
			return true;
		}
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, "onKeyDown " + keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_ESCAPE:
			showExitDialog();
			break;
		case KeyEvent.KEYCODE_MENU:
			showConfigDialog();
			break;
		case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			playLastEpisode();
			break;
		case KeyEvent.KEYCODE_MEDIA_NEXT:
			playNextEpisode();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	};

	*//**
	 * 请在在播放器的"onPrepared()"回调中调用
	 * <p>
	 * 本方法主要封装了如下逻辑:<br>
	 * 1. 更新controller中总时长<br>
	 * 2. 播放指定进度 or 自动断点续播 并 显示播放历史对话框<br>
	 * 4. 如果用户未手动暂停，则自动播放
	 * 
	 * @param playIndex
	 *//*
	protected void onPlayerPrepared() {
		if (mVideoPlayer.isPlayingAdvert()) {
			Log.d(TAG, "AD is playing onPlayerPrepared.");
			return;
		}
		isPlayerPrepared = true;

		VideoPlayerController uiControl = null;
		if (mUIControl != null && mUIControl instanceof VideoPlayerController) {
			uiControl = (VideoPlayerController) mUIControl;
		}
		if (uiControl != null) {
			int duration = mVideoPlayer.getDuration();
			uiControl.updateDuration(duration);
			uiControl.updateProgressUI();
			uiControl.show(DEFAULT_CONTROLLER_SHOW_TIME);
			uiControl.setSeekGap(duration / 100);
		}

		// 缓冲阶段点击暂停按钮，缓冲结束不能自动播放
		if (uiControl == null || !uiControl.isPausedUIVisible() || !mVideoPlayer.isPaused()) {
			mVideoPlayer.start();
		} else {
			if (mVideoPlayer != null && mVideoPlayer instanceof CanVideoView) {
				// 原生播放器在"PREPARED"状态下无法直接调用PAUSE
				mVideoPlayer.start();
			}
			mVideoPlayer.pause();
		}

		if (destPosition > 0) {
			mVideoPlayer.seekTo(destPosition);
			destPosition = 0;

		} else if (mFreePlayEpisode <= 0 && mFreePlayTime <= 0 && mPlayType != PlayConstants.PLAY_TYPE.PUSH) {
			// 非试看，则显示播放历史提示框，且自动从历史纪录开始播放
			int historyPlayProgress = queryPlayProgress(mCurrPlayIndex);
			if (historyPlayProgress > 0) {
				mVideoPlayer.seekTo(historyPlayProgress);
				if (firstShowProgressDialog) {
					showPlayHistoryDialog(historyPlayProgress);
				}
			}
		}
		firstShowProgressDialog = false;
	}

	*//**
	 * 记录播放器是否暂停的状态，并在切换剧集后保持<br>
	 * 此方法用来解决播放器暂停后，切换信号源，再次回到播放器需要保持暂停状态的问题<br>
	 * ps：在播放影片前调用此方法
	 *//*
	private void notifyKeepPlayerPauseStatus() {
		keepPlayerPauseStatus = true;
	}

	final protected void onPlayerSeekComplete() {
		if (mVideoPlayer != null) {
			if (mUIControl != null && mUIControl instanceof VideoPlayerController) {
				VideoPlayerController uiController = (VideoPlayerController) mUIControl;
				uiController.updateDuration(mVideoPlayer.getDuration());
				uiController.updateProgressUI();
				if (uiController.isPausedUIVisible()) {
					mVideoPlayer.pause();
					return;
				}
			}
			if (mVideoPlayer.isPaused()) {
				mVideoPlayer.pause();
				return;
			}
			mVideoPlayer.start();
		}
	}

	*//**
	 * 节目播放完成后的处理逻辑
	 * <p>
	 * 1. 存储播放进度 2. 自动播放下一集，如果
	 *//*
	final protected void onPlayComplete() {
		isPlayerCompletion = true;
		playNextEpisode();
	}*/
}
