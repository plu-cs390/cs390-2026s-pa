package bchart.ui;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 *  The {@code BarChart} class represents a panel that displays a bar chart.
 *  It supports adding a bar (with a specified name, value, and category) and
 *  drawing all of the bars to the screen using standard draw.
 *  The bars are drawn horizontally (in the order in which they are added from
 *  top to bottom) and colored according to the category.
 *  The name and value of the bar and drawn with the bar.
 *
 *  @author Kevin Wayne with modifications by David Wolff
 */
public class BarChart {

    // color palette for bars
    private static final Color[] COLORS = initColors();

    private String title;               // bar chart title
    private String xAxisLabel;          // x-axis label
    private String dataSource;          // data source
    private String caption;                   // caption
    private TreeMap<String, Color> colorOf;   // map category to color
    private ArrayList<String> names;          // list of bar names
    private ArrayList<Integer> values;        // list of bar values
    private ArrayList<Color> colors;          // list of bar colors
    private boolean isSetMaxValue = false;
    private int maxValue = 0;

    /**
     * Creates a new bar chart with empty title, xAxisLabel and source.
     */
    public BarChart() {
        this.title = "No title";
        this.xAxisLabel = "";
        this.dataSource = "";
        colorOf = new TreeMap<String, Color>();
        reset();
    }

    public void setXAxisLabel( String label ) { this.xAxisLabel = label; }
    public void setDataSource( String source ) { this.dataSource = source; }

    public void setTitle( String title ) { this.title = title; }

    // initialize the colors
    private static Color[] initColors() {

        // 12 colors from http://colorbrewer2.org/#type=qualitative&scheme=Set3&n=12
        String[] hex12 = {
                "#80b1d3", "#fdb462", "#b3de69", "#fccde5",
                "#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
                "#d9d9d9", "#bc80bd", "#ccebc5", "#ffed6f"
        };

        // 20 colors from https://vega.github.io/vega/docs/schemes/
        // replaced #d62728 with #d64c4c
        String[] hex20 = {
                "#aec7e8", "#c5b0d5", "#c49c94", "#dbdb8d", "#17becf",
                "#9edae5", "#f7b6d2", "#c7c7c7", "#1f77b4", "#ff7f0e",
                "#ffbb78", "#98df8a", "#d64c4c", "#2ca02c", "#9467bd",
                "#8c564b", "#ff9896", "#e377c2", "#7f7f7f", "#bcbd22",
        };

        // use 20 colors
        Color[] colors = new Color[hex20.length];
        for (int i = 0; i < hex20.length; i++)
            colors[i] = Color.web(hex20[i]);
        return colors;
    }

    /**
     * Sets the maximum x-value of this bar chart (instead of having it set automatially).
     * This method is useful if you know that the values stay within a given range.
     *
     * @param maxValue the maximum value
     */
    public void setMaxValue(int maxValue) {
        if (maxValue <= 0) throw new IllegalArgumentException("maximum value must be positive");
        this.isSetMaxValue = true;
        this.maxValue = maxValue;
    }

    /**
     * Sets the caption of this bar chart.
     * The caption is drawn in the lower-right part of the window.
     *
     * @param caption the caption
     */
    public void setCaption(String caption) {
        if (caption == null) throw new IllegalArgumentException("caption is null");
        this.caption = caption;
    }

    /**
     * Adds a bar to the bar chart.
     * The length of a bar is proportional to its value.
     * The bars are drawn from top to bottom in the order they are added.
     * All bars from the same category are drawn with the same color.
     *
     * @param name     the name of the bar
     * @param value    the value of the bar
     * @param category the category of bar
     */
    public void add(String name, int value, String category) {
        if (name == null) throw new IllegalArgumentException("name is null");
        if (category == null) throw new IllegalArgumentException("category is null");
        if (value <= 0) throw new IllegalArgumentException("value must be positive");

        if (!colorOf.containsKey(category)) {
            colorOf.put(category, COLORS[colorOf.size() % COLORS.length]);
        }
        Color color = colorOf.get(category);
        names.add(name);
        values.add(value);
        colors.add(color);
    }

    /**
     * Removes all of the bars from this bar chart (but keep the color scheme).
     * This method is convenient when drawing an animated bar chart.
     */
    public void reset() {
        names = new ArrayList<String>();
        values = new ArrayList<Integer>();
        colors = new ArrayList<Color>();
        caption = "";
    }

    // compute units (multiple of 1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, ...)
    // so that between 4 and 8 axes labels
    private static int getUnits(double xmax) {
        int units = 1;
        while (Math.floor(xmax / units) >= 8) {
            // hack to identify 20, 200, 2000, ...
            if (units % 9 == 2)  units = units * 5 / 2;
            else                 units = units * 2;
        }
        return units;
    }

    /**
     * Draws this bar chart to the provided GraphicsContext.
     *
     * @param g A GraphicsContext to draw with.
     * @param width The width of the canvas
     * @param height The height of the canvas
     */
    public void draw( GraphicsContext g, int width, int height ) {
        // nothing to draw
        if (names.isEmpty()) return;

        g.clearRect(0, 0, width, height);

        final int LEFT_MARGIN = 10;
        final int RIGHT_MARGIN = 80;
        final int VERT_MARGIN = 10;
        int chartWidth = width - LEFT_MARGIN - RIGHT_MARGIN;

        // leave room for at least 8 bars
        int numberOfBars = Math.max(8, names.size());

        // set the scale of the coordinate axes
        double xmax = Double.NEGATIVE_INFINITY;
        for (int value : values) {
            if (value > xmax) xmax = value;
        }
        if (isSetMaxValue) xmax = maxValue;

        // draw title
        g.setFill(Color.BLACK);
        g.setFont(Font.font("SansSerif", FontWeight.BOLD, 24));
        g.fillText(title, 45, 35);

        // draw x-axis label
        g.setFill(Color.GRAY);
        g.setFont(Font.font("SansSerif", FontWeight.NORMAL, 16));
        g.fillText(xAxisLabel, 10, 75);

        // draw axes
        int units = getUnits(xmax);
        g.setFont(Font.font("SansSerif", FontWeight.NORMAL, 12));
        for (int unit = 0; unit <= xmax; unit += units) {
            int x = (int)( ((double)unit / xmax) * chartWidth + LEFT_MARGIN );
            g.setFill(Color.GRAY);
            g.setTextAlign(TextAlignment.CENTER);
            g.setStroke(Color.rgb(230, 230, 230));
            g.fillText(String.format("%,d", unit), x, 100);
            g.strokeLine(x, 105, x, height - VERT_MARGIN );
        }

        // draw caption
        g.setFill(Color.LIGHTGRAY);
        if      (caption.length() <= 4) g.setFont(Font.font("SansSerif", FontWeight.BOLD, 100));
        else if (caption.length() <= 8) g.setFont(Font.font("SansSerif", FontWeight.BOLD, 60));
        else                            g.setFont(Font.font("SansSerif", FontWeight.BOLD, 40));
        g.setTextAlign(TextAlignment.RIGHT);
        g.fillText(caption, width - RIGHT_MARGIN, 500);

        // draw data source acknowledgment
        g.setFill(Color.LIGHTGRAY);
        g.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        g.setTextAlign(TextAlignment.RIGHT);
        g.fillText(dataSource, width - RIGHT_MARGIN, 550);

        final int BAR_HEIGHT = 40;
        final int BAR_MARGIN = 10;
        // draw bars
        g.setTextBaseline(VPos.CENTER);
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            int value = values.get(i);
            Color color = colors.get(i);
            g.setFill(color);
            int barWidth = (int)( (value / xmax) * chartWidth );
            int barY = 115 + i * (BAR_HEIGHT + BAR_MARGIN);
            g.fillRect(LEFT_MARGIN, barY, barWidth, BAR_HEIGHT);
            g.setFill(Color.BLACK);
            int fontSize = (int) Math.ceil(14 * 10.0 / numberOfBars);
            g.setFont(Font.font("SansSerif", FontWeight.BOLD, fontSize));
            g.setTextAlign(TextAlignment.RIGHT);
            g.fillText(name, barWidth + LEFT_MARGIN - 5, barY + BAR_HEIGHT / 2);
            g.setFont(Font.font("SansSerif", FontWeight.NORMAL, fontSize));
            g.setFill(Color.DARKGRAY);
            g.setTextAlign(TextAlignment.LEFT);
            g.fillText(String.format("%,d", value), barWidth + LEFT_MARGIN + 5, barY + BAR_HEIGHT / 2);
        }
    }
}