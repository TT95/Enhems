/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphjob;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.legends.Legend;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.util.Insets2D;

/**
 *
 * @author Stjepan
 */
public class Graph implements AutoCloseable {

    /**
     * Constructor, creates graph object
     *
     * @param graphData data to be shown on graph
     * @param seriesName series name to be shown in the legend
     * @param xLabels X-Axis labels
     */
    @SuppressWarnings("unchecked")
	public Graph(Map<Integer, Double> graphData, String seriesName, Map<Double, String> xLabels) {
        this.xLabels = xLabels;
        DataTable data = new DataTable(Double.class, Double.class);
        DataTable help = new DataTable(Double.class, Double.class);
        double i;
        double max = 0;
        double min = -1;
        //data.add(0.0, 0.0);
        Object[] values = graphData.values().toArray();
        for (i = 0.0; i < values.length; i++) {
            double value = (double) values[(int) i];
            if (i > 0 && value > 0 && (double) values[(int) (i - 1)] == 0) {
                data.add(i, 0.0);
            }
            data.add(i, value);
            if (i < values.length - 1 && value > 0 && (double) values[(int) (i + 1)] == 0) {
                data.add(i, 0.0);
            }
            if (value > max) {
                max = value;
            }
            if (value < min || min==-1) {
                min = value;
            }
        }
        min/=1.05;
        data.add(i - 1, min);
        data.add(i, min);
        help.add(0.0, max * 1.01);
        DataSeries series = new DataSeries(seriesName, data, 0, 1);
        plot = new XYPlot(series, help);
        PointRenderer points1 = new DefaultPointRenderer2D();
        points1.setShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        points1.setColor(new Color(0.0f, 0.3f, 1.0f, 0.3f));
        plot.setPointRenderer(series, points1);
        Style(series, help, min);
    }

    /**
     * Implementation of AutoCloseable, clears graph object attributes
     */
    @Override
    public void close() {
        plot.clear();
        plot = null;
        xLabels.clear();
        xLabels = null;
    }

    /**
     * Saves graph object as image at given path/filename
     *
     * @param filename path/filename
     * @throws IOException
     */
    public void Save(String filename) throws IOException {
        DrawableWriter writer = DrawableWriterFactory.getInstance().get("image/png");
        try (FileOutputStream outStream = new FileOutputStream(filename)) {
            writer.write(plot, outStream, 1280, 720);
        }
    }

    /**
     * Customizes graph, color, font, legend....
     *
     * @param series main data series, visible
     * @param help help series, not visible
     */
    private void Style(DataSource series, DataSource help, double min) {
    	SetAreaRenderer(series);
        SetPointsInvisible(series, help);
        SetBackground();
        SetLegend(help);
        plot.setInsets(new Insets2D.Double(65, 90, 65, 65));
        SetCustomXLabels();
        StyleAxis(plot.getAxisRenderer(XYPlot.AXIS_X));
        StyleAxis(plot.getAxisRenderer(XYPlot.AXIS_Y));
        SetOrigin(min);
    }

    /**
     * Set invisible point markers
     *
     * @param series main data series, visible
     * @param help help series, not visible
     */
    private void SetPointsInvisible(DataSource series, DataSource help) {
        plot.getPointRenderer(series).setColor(new Color(0.0f, 0.0f, 0.0f, 0f));
        plot.getPointRenderer(help).setColor(new Color(0.0f, 0.0f, 0.0f, 0f));
    }

    /**
     * Set background color and grid color
     */
    private void SetBackground() {
        plot.setBackground(Color.white);
        plot.getPlotArea().setBackground(Color.white);
        ((XYPlot.XYPlotArea2D) plot.getPlotArea()).setMajorGridColor(Color.black);
    }

    /**
     * Set area graph for data series
     *
     * @param series data series for area graph
     */
    private void SetAreaRenderer(DataSource series) {
        AreaRenderer area = new DefaultAreaRenderer2D();
        area.setColor(new Color(255, 140, 0, 200));
        plot.setAreaRenderer(series, area);
    }

    /**
     * Set legend
     *
     * @param help help data series which will be removed from the legend
     */
    private void SetLegend(DataSource help) {
        plot.setLegendVisible(true);
        Legend legend = plot.getLegend();
        legend.setAlignmentX(1.0);
        legend.setAlignmentY(1.0);
        legend.remove(help);
        legend.setFont(legend.getFont().deriveFont(20.0f));
    }

    /**
     * Set custom X-axis labels
     */
    private void SetCustomXLabels() {
        AxisRenderer rendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        rendererX.setTicksAutoSpaced(false);
        rendererX.setTickSpacing(24);
        rendererX.setCustomTicks(xLabels);
    }

    /**
     * Set axis color, and label font
     *
     * @param axis axis renderer to customize
     */
    private void StyleAxis(AxisRenderer axis) {
        axis.setShapeColor(Color.black);
        axis.setTickColor(Color.black);
        axis.setMinorTickColor(Color.black);
        axis.setTickFont(axis.getTickFont().deriveFont(20.0f));
    }

    /**
     * Set plot origin
     * @param min origin value
     */
    private void SetOrigin(double min) {
        AxisRenderer rendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        rendererX.setIntersection(min);
    }

    private XYPlot plot;
    private Map<Double, String> xLabels;
}
