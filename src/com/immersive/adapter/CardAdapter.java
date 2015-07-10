package com.immersive.adapter;

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

import com.code.immersivemode.R;
import com.code.immersivemode.Step;
import com.immersive.activity.ChartActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CardAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Step> mDailyStep;
	
	private XYMultipleSeriesDataset mDataset = null;
	private XYMultipleSeriesRenderer mRenderer = null;
	private XYSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;
	private GraphicalView mChartView;
	
	private int sum_step = 0;
	
	public CardAdapter(Context context, List<Step> dailyStep) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mDailyStep = dailyStep;
		}
		

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDailyStep.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	static class ViewHolder {
		public LinearLayout iv_chart;
		public TextView tv_date;
		public TextView tv_step;
		public ViewHolder(View view) {
			iv_chart = (LinearLayout) view.findViewById(R.id.iv_chart);
			tv_date = (TextView) view.findViewById(R.id.card_date);
			tv_step = (TextView) view.findViewById(R.id.card_content);
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh;

//		if(convertView != null) {
//			vh = (ViewHolder)convertView.getTag();
//		} else {
		
			convertView = mInflater.inflate(R.layout.item_card, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
//		}
		
		
		
		initChart(vh, position);
		addSeries();
		loadData(vh, position);
		vh.iv_chart.setTag(mDailyStep.get(position).getStep_date());
		DateFormat format = new SimpleDateFormat("yyyy:MM:dd");
		try {
			Date date = format.parse(mDailyStep.get(position).getStep_date());
			Date currentTime = new Date();
			if (currentTime.getTime() - date.getTime() < 86400000) {
				vh.tv_date.setText("Today");
			} else if (currentTime.getTime() - date.getTime() < 86400000 * 2) {
				vh.tv_date.setText("Yesterday");
			} else {
				vh.tv_date.setText((mDailyStep.get(position).getStep_date().replace(":", "-")));
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		vh.tv_step.setText(mContext.getString(R.string.step) + "  " + sum_step);
		
		return convertView;
	}

	
	private void initChart(ViewHolder vh, int position) {
		
		mDataset = new XYMultipleSeriesDataset();
		mRenderer = new XYMultipleSeriesRenderer();
		
		
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
		mRenderer.setLabelsTextSize(30);
		mRenderer.setShowGridY(false);
//		mRenderer.setZoomRate(0.2f);
		mRenderer.setZoomButtonsVisible(false);
		
		
		mChartView = ChartFactory.getLineChartView(mContext, mDataset, mRenderer);
		mChartView.setTag(mDailyStep.get(position).getStep_date());
		mRenderer.setClickEnabled(true);// 设置图表是否允许点击
		mRenderer.setZoomEnabled(false);
		mRenderer.setPanEnabled(false);
		mRenderer.setInScroll(false);
		if (mChartView.getTag() == mDailyStep.get(position).getStep_date()) {
//			Log.e("adapter", "Tag same: add view");
			vh.iv_chart.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
//			Log.e("adapter", "Tag error");
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
		renderer.setColor(mContext.getResources().getColor(R.color.app_theme_color_deep));
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
	
	private void loadData(ViewHolder vh, int position) {
		sum_step = 0;
		Step mCurrentStep = mDailyStep.get(position);
		for (int i = 0; i < 24; i++) {
			Field stepField;
			try {
				stepField = Step.class.getDeclaredField("step_"+i);
				stepField.setAccessible(true);
				int stepCount = (Integer) stepField.get(mCurrentStep);
				sum_step += stepCount;
				mCurrentSeries.add(i, (double) stepCount);
//				Log.d("adapter", "step_" + i + "=>" + stepField.get(mCurrentStep));
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
		if (mChartView.getTag() == mDailyStep.get(position).getStep_date()) {
			mChartView.repaint();// 重画图表
//			Log.e("adapter", "repaint");
		} else {
//			Log.e("adapter", "not repaint");
		}
	}

}
