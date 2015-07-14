package com.immersive.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import com.code.immersivemode.AppContext;
import com.code.immersivemode.R;
import com.code.immersivemode.Step;
import com.immersive.utils.GreenDaoUtils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChartActivity extends SneakerDialogActivity {
	public static final String TAG = "ChartActivity";
	public static final String TYPE = "type";
	
	

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;
	private GraphicalView mChartView;
	
	private String mDateFormat;
	
	private TextView topbar_title = null;
	private ImageView topbar_back, topbar_next, topbar_previous = null;
	private View.OnClickListener mOnClickListener = null;
	
	private GreenDaoUtils mDBUtils = null;
	private long current_id = -1;
	private List<Step> mDailyStep = null;
	private Step mCurrentStep = null;
	private int mCurrentPos = -1;
	private String mCurrentDate = "";

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mDataset = (XYMultipleSeriesDataset) savedState
				.getSerializable("dataset");
		mRenderer = (XYMultipleSeriesRenderer) savedState
				.getSerializable("renderer");
		mCurrentSeries = (XYSeries) savedState
				.getSerializable("current_series");
		mCurrentRenderer = (XYSeriesRenderer) savedState
				.getSerializable("current_renderer");
		mDateFormat = savedState.getString("date_format");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("dataset", mDataset);
		outState.putSerializable("renderer", mRenderer);
		outState.putSerializable("current_series", mCurrentSeries);
		outState.putSerializable("current_renderer", mCurrentRenderer);
		outState.putString("date_format", mDateFormat);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_chart);
		
		Intent intent = getIntent();
		current_id = intent.getLongExtra("step_id", -1);
		mCurrentPos = intent.getIntExtra("position", -1);
		topbar_title = (TextView) findViewById(R.id.topbar_title);
		
		initChart();
		initListener();
		
		addSeries();
		loadData();
//		testPoint();
		initWidget();
		
	}
	
	private void loadData() {
		mDBUtils = GreenDaoUtils.getInstance(this);
		if (current_id == -1) {
			Log.e(TAG, "step_id error");
			return;
		}
		mDailyStep = mDBUtils.getAllStep(AppContext.user_id);
		mCurrentDate = mDailyStep.get(mCurrentPos).getStep_date();
		
		DateFormat format = new SimpleDateFormat("yyyy:MM:dd");
		try {
			Date date = format.parse(mDailyStep.get(mCurrentPos).getStep_date());
			Date currentTime = new Date();
			if (currentTime.getTime() - date.getTime() < 86400000) {
				topbar_title.setText(getString(R.string.today));
			} else if (currentTime.getTime() - date.getTime() < 86400000 * 2) {
				topbar_title.setText(getString(R.string.yesterday));
			} else {
				topbar_title.setText((mDailyStep.get(mCurrentPos).getStep_date().replace(":", "-")));
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		mCurrentStep = mDBUtils.getStepById(current_id);
		mCurrentStep = mDailyStep.get(mCurrentPos);
		mCurrentSeries.clear();
		for (int i = 0; i < 24; i++) {
			Field stepField;
			try {
				stepField = Step.class.getDeclaredField("step_"+i);
				stepField.setAccessible(true);
				int stepCount = (Integer) stepField.get(mCurrentStep);
				mCurrentSeries.add(i, (double) stepCount);
//				Log.d(TAG, "step_" + i + "=>" + stepField.get(mCurrentStep));
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (mChartView != null) {
			mChartView.repaint();// 重画图表
		}
		
	}
	
	private void initWidget() {
		
		
		
		topbar_back = (ImageView) findViewById(R.id.topbar_opv);
		topbar_back.setOnClickListener(mOnClickListener);
		topbar_next = (ImageView) findViewById(R.id.topbar_next);
		topbar_next.setOnClickListener(mOnClickListener);
		topbar_previous = (ImageView) findViewById(R.id.topbar_pre);
		topbar_previous.setOnClickListener(mOnClickListener);
		
		
		if (mDailyStep.size() == 1) {
			topbar_previous.setVisibility(View.INVISIBLE);
			topbar_next.setVisibility(View.INVISIBLE);
		} else if (mCurrentPos == 0) {
			topbar_previous.setVisibility(View.INVISIBLE);
		} else if (mCurrentPos == mDailyStep.size() - 1){
			topbar_next.setVisibility(View.INVISIBLE);
		}
		
	}

	private void initChart() {
		mRenderer.setApplyBackgroundColor(true);// 设置是否显示背景色
		mRenderer.setBackgroundColor(Color.WHITE);// 设置背景色
		mRenderer.setMarginsColor(Color.WHITE);		// 设置边框背景色
		mRenderer.setAxisTitleTextSize(30); // 设置轴标题文字的大小
		mRenderer.setChartTitleTextSize(30);// ?设置整个图表标题文字大小
		mRenderer.setLabelsTextSize(16);// 设置刻度显示文字的大小(XY轴都会被设置)
		mRenderer.setLegendTextSize(30);// 图例文字大小
		mRenderer.setShowGrid(true);
		mRenderer.setGridColor(Color.BLACK);
		mRenderer.setMargins(new int[] { 30, 80, 30, 40 });// 设置图表的外边框(上/左/下/右)
		mRenderer.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
		mRenderer.setPointSize(10);// 设置点的大小(图上显示的点的大小和图例中点的大小都会被设置)
		mRenderer.setAxesColor(Color.RED);
//		mRenderer.setRange(new double[] { -1, 24, -1, 500 });
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setYLabelsPadding(22);
		mRenderer.setXLabelsPadding(2);
		mRenderer.setYTitle("Step");
		mRenderer.setXTitle("Hour");
		mRenderer.setChartTitle("Daily Step Data");
		mRenderer.setLabelsTextSize(30);
		mRenderer.setShowGridY(false);
//		mRenderer.setZoomRate(0.2f);
		mRenderer.setZoomButtonsVisible(false);

		
	}
	
	private void initListener() {
		
		mOnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.topbar_opv:
					finish();
					break;
				case R.id.topbar_next:
					showNext();
					break;
				case R.id.topbar_pre:
					showPrevious();
					break;
				}
			}
			
		};
		
		
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);
			mRenderer.setClickEnabled(true);// 设置图表是否允许点击
			mRenderer.setSelectableBuffer(100);// 设置点的缓冲半径值(在某点附件点击时,多大范围内都算点击这个点)
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 这段代码处理点击一个点后,获得所点击的点在哪个序列中以及点的坐标.
					// -->start
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					double[] xy = mChartView.toRealPoint(0);
					if (seriesSelection == null) {
						Toast.makeText(ChartActivity.this,
								"No chart element was clicked",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								ChartActivity.this,
								"Chart element in series index "
										+ seriesSelection.getSeriesIndex()
										+ " data point index "
										+ seriesSelection.getPointIndex()
										+ " was clicked"
										+ " closest point value X="
										+ seriesSelection.getXValue() + ", Y="
										+ seriesSelection.getValue()
										+ " clicked point value X="
										+ (float) xy[0] + ", Y="
										+ (float) xy[1], Toast.LENGTH_SHORT)
								.show();
					}
					// -->end
				}
			});
			mChartView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(ChartActivity.this,
								"No chart element was long pressed",
								Toast.LENGTH_SHORT);
						return false; // no chart element was long pressed, so
										// let something
						// else handle the event
					} else {
						Toast.makeText(ChartActivity.this,
								"Chart element in series index "
										+ seriesSelection.getSeriesIndex()
										+ " data point index "
										+ seriesSelection.getPointIndex()
										+ " was long pressed",
								Toast.LENGTH_SHORT);
						return true; // the element was long pressed - the event
										// has been
						// handled
					}
				}
			});
			// 这段代码处理放大缩小
			// -->start
			mChartView.addZoomListener(new ZoomListener() {
				public void zoomApplied(ZoomEvent e) {
					String type = "out";
					if (e.isZoomIn()) {
						type = "in";
					}
					System.out.println("Zoom " + type + " rate "
							+ e.getZoomRate());
				}

				public void zoomReset() {
					System.out.println("Reset");
				}
			}, true, true);
			// -->end
			// 设置拖动图表时后台打印出图表坐标的最大最小值.
			mChartView.addPanListener(new PanListener() {
				public void panApplied() {
					System.out.println("New X range=["
							+ mRenderer.getXAxisMin() + ", "
							+ mRenderer.getXAxisMax() + "], Y range=["
							+ mRenderer.getYAxisMax() + ", "
							+ mRenderer.getYAxisMax() + "]");
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			boolean enabled = mDataset.getSeriesCount() > 0;
		} else {
			mChartView.repaint();
		}
	}
	
	private void addSeries() {
		String seriesTitle = "Step" + (mDataset.getSeriesCount() + 1);// 图例
		XYSeries series = new XYSeries(seriesTitle);	// 定义XYSeries
		mDataset.addSeries(series);	// 在XYMultipleSeriesDataset中添加XYSeries
		mCurrentSeries = series;	// 设置当前需要操作的XYSeries
		XYSeriesRenderer renderer = new XYSeriesRenderer();	// 定义XYSeriesRenderer
		mRenderer.addSeriesRenderer(renderer);	// 将单个XYSeriesRenderer增加到XYMultipleSeriesRenderer
		renderer.setPointStyle(PointStyle.CIRCLE);	// 点的类型是圆形
		renderer.setColor(Color.RED);
		renderer.setLineWidth(10);
		renderer.setFillPoints(true);// 设置点是否实心
		
		mCurrentRenderer = renderer;

	}
	
	private void showNext() {
		if (mCurrentPos == -1 || mCurrentPos == mDailyStep.size() - 1) {
			return;
		} else {
			mCurrentPos++;
			//mCurrentStep = mDailyStep.get(mCurrentPos);
			loadData();
			topbar_previous.setVisibility(View.VISIBLE);
		}
		if (mCurrentPos == mDailyStep.size() - 1) {
			topbar_next.setVisibility(View.INVISIBLE);
		}
	}
	
	private void showPrevious() {
		if (mCurrentPos == -1 || mCurrentPos == 0) {
			return;
		} else {
			mCurrentPos--;
			//mCurrentStep = mDailyStep.get(mCurrentPos);
			loadData();
			topbar_next.setVisibility(View.VISIBLE);
		}
		if (mCurrentPos == 0) {
			topbar_previous.setVisibility(View.INVISIBLE);
		} 
	}
	
	private void enerateBitmap() {
		// 生成图片保存,注释掉下面的代码不影响图表的生成.
		// -->start
		Bitmap bitmap = mChartView.toBitmap();
		try {
			File file = new File(AppContext.PATH + File.separator + "Chart_" + System.currentTimeMillis()
					+ ".png");
			FileOutputStream output = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// -->end
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	

}