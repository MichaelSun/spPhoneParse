package com.xstd.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by michael on 14-1-20.
 */
public class BarChartUtils {

    public static final class BarChartObject {

        public String colum;

        public String row;

        public int value;
    }

    public static CategoryDataset createDataSet(List<BarChartObject> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (BarChartObject d : data) {
            dataset.setValue(d.value, d.row, d.colum);
        }

        return dataset;
    }

    /**
     * step2:创建图表
     *
     * @param dataset
     * @return
     */
    public static JFreeChart createChart(String title, String columName, String rowName, CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(   //3D柱状图
                                                          //JFreeChart chart = ChartFactory.createLineChart3D(  //3D折线图
                                                          title, //图表的标题
                                                          columName,  //目录轴的显示标签
                                                          rowName,   //数值轴的显示标签
                                                          dataset, //数据集
                                                          PlotOrientation.VERTICAL,  //图表方式：V垂直;H水平
                                                          true, // 是否显示图例
                                                          false, // 是否显示工具提示
                                                          false // 是否生成URL
        );

        //===============为了防止中文乱码：必须设置字体
        chart.setTitle(new TextTitle(title, new Font("黑体", Font.ITALIC, 22)));

        LegendTitle legend = chart.getLegend(); // 获取图例
        legend.setItemFont(new Font("宋体", Font.BOLD, 12)); //设置图例的字体，防止中文乱码

        CategoryPlot plot = (CategoryPlot) chart.getPlot(); // 获取柱图的Plot对象(实际图表)
        // 设置柱图背景色（注意，系统取色的时候要使用16位的模式来查看颜色编码，这样比较准确）
        plot.setBackgroundPaint(new Color(255, 255, 204));
        plot.setForegroundAlpha(0.65F); //设置前景色透明度

        // 设置横虚线可见
        plot.setRangeGridlinesVisible(true);
        // 虚线色彩
        plot.setRangeGridlinePaint(Color.gray);

        CategoryAxis h = plot.getDomainAxis(); //获取x轴
        h.setMaximumCategoryLabelWidthRatio(1.0f);// 横轴上的 Lable 是否完整显示
        h.setLabelFont(new Font("宋体", Font.BOLD, 10));//设置字体，防止中文乱码
        h.setTickLabelFont(new Font("宋体", Font.BOLD, 12));// 轴数值
        h.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);//45度倾斜

        plot.getRangeAxis().setLabelFont(new Font("宋体", Font.BOLD, 12)); //Y轴设置字体，防止中文乱码

        //柱图的呈现器
        StackedBarRenderer renderer = new StackedBarRenderer();
        // 设置柱子宽度
//        renderer.setMaximumBarWidth(0.05);
//        设置柱子高度
        //renderer.setMinimumBarLength(0.2);
        // 设置柱子边框颜色
        renderer.setBaseOutlinePaint(Color.BLACK);
        // 设置柱子边框可见
        renderer.setDrawBarOutline(true);
        //设置每个柱的颜色
//        renderer.setSeriesPaint(0, Color.BLUE);
//        renderer.setSeriesPaint(1, Color.GREEN);
//        renderer.setSeriesPaint(2, Color.RED);
        //设置每个地区所包含的平行柱的之间距离
        renderer.setItemMargin(0.05);
        //显示每个柱的数值，并修改该数值的字体属性
        renderer.setIncludeBaseInRange(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        // 设置柱的透明度
        plot.setForegroundAlpha(1.0f);
        //给柱图添加呈现器
        plot.setRenderer(renderer);

        return chart;
    }

    /**
     * step3: 输出图表到指定的磁盘
     *
     * @param destPath
     * @param chart
     */
    public static void drawToOutputStream(String destPath, JFreeChart chart) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destPath);
            // ChartUtilities.writeChartAsJPEG(
            ChartUtilities.writeChartAsPNG(fos, // 指定目标输出流
                                              chart, // 图表对象
                                              720 * 10, // 宽
                                              720, // 高
                                              null); // ChartRenderingInfo信息
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void buildChar(String charFullPath, String title, String columName, String rowName, List<BarChartObject> data) {
        // step1:创建数据集对象
        CategoryDataset dataset = createDataSet(data);

        // step2:创建图表
        JFreeChart chart = createChart(title, columName, rowName, dataset);

        // step3: 输出图表到Swing窗口
        //drawToFrame(chart);

        // step3: 输出图表到磁盘
        drawToOutputStream(charFullPath, chart);
    }

}
