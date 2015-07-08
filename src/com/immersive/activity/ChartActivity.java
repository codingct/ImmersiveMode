package com.immersive.activity;

import java.io.File;
import java.io.FileOutputStream;

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

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ChartActivity extends Activity {
	public static final String TYPE = "type";

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;

	private GraphicalView mChartView;
	
	private String mDateFormat;
	
	private ImageView topbar_back, topbar_next, topbar_previous = null;
	private View.OnClickListener mOnClickListener = null;

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

		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.page_chart);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参值
		p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.8
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.0f; // 设置黑暗度
		getWindow().setAttributes(p); // 设置生效

		
		initChart();
		initListener();
		initWidget();
		
		addSeries();
		testPoint();
		
	}
	
	private void initWidget() {
		topbar_back = (ImageView) findViewById(R.id.topbar_opv);
		topbar_back.setOnClickListener(mOnClickListener);
		topbar_next = (ImageView) findViewById(R.id.topbar_next);
		topbar_next.setOnClickListener(mOnClickListener);
		topbar_previous = (ImageView) findViewById(R.id.topbar_pre);
		topbar_previous.setOnClickListener(mOnClickListener);
		
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
					break;
				case R.id.topbar_pre:
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
	
	private void testPoint() {
		mCurrentSeries.add(0, 87);
		mCurrentSeries.add(1, 23);
		mCurrentSeries.add(2, 0);
		mCurrentSeries.add(3, 0);
		mCurrentSeries.add(4, 0);
		mCurrentSeries.add(5, 0);
		mCurrentSeries.add(6, 0);
		mCurrentSeries.add(7, 0);
		mCurrentSeries.add(8, 91);
		mCurrentSeries.add(9, 197);
		mCurrentSeries.add(10, 263);
		mCurrentSeries.add(11, 136);
		mCurrentSeries.add(12, 370);
		mCurrentSeries.add(13, 189);
		mCurrentSeries.add(14, 37);
		mCurrentSeries.add(15, 0);
		mCurrentSeries.add(16, 92);
		mCurrentSeries.add(17, 12);
		mCurrentSeries.add(18, 189);
		mCurrentSeries.add(19, 271);
		mCurrentSeries.add(20, 386);
		mCurrentSeries.add(21, 14);
		mCurrentSeries.add(22, 19);
		mCurrentSeries.add(23, 87);
		if (mChartView != null) {
			mChartView.repaint();// 重画图表
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
	
	@Override
	public void finish() {
		super.finish();
		this.setResult(RESULT_OK);
	}

}