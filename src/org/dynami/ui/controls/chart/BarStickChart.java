package org.dynami.ui.controls.chart;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.dynami.core.data.Bar;

import extfx.scene.chart.DateAxis;
import javafx.animation.FadeTransition;
import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class BarStickChart extends XYChart<Date, Number> {
    /**
     * Construct a new BarStickChart with the given axis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public BarStickChart(@NamedArg("xAxis") Axis<Date> xAxis, @NamedArg("yAxis") Axis<Number> yAxis) {
        super(xAxis, yAxis);
        setAnimated(false);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);

        getStylesheets().add("org/dynami/ui/controls/chart/barstick-stylesheet.css");
    }

    /**
     * Construct a new CandleStickChart with the given axis and data.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @param data The data to use, this is the actual list used so any changes to it will be reflected in the chart
     */
    public BarStickChart(@NamedArg("xAxis") Axis<Date> xAxis, @NamedArg("yAxis") Axis<Number> yAxis, @NamedArg("data") ObservableList<Series<Date, Number>> data) {
        this(xAxis, yAxis);
        setData(data);
    }

    /** Called to update and layout the content for the plot */
    @Override protected void layoutPlotChildren() {
        // we have nothing to layout if no data is present
        if (getData() == null) {
            return;
        }
        // update candle positions
        for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
            Series<Date, Number> series = getData().get(seriesIndex);
            Iterator<Data<Date, Number>> iter = getDisplayedDataIterator(series);
            while (iter.hasNext()) {
                Data<Date, Number> item = iter.next();
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                Node itemNode = item.getNode();
                Bar extra = (Bar) item.getExtraValue();
                if (itemNode instanceof BarStick && extra != null) {
                    BarStick node = (BarStick) itemNode;
                    double open = getYAxis().getDisplayPosition(extra.getOpen());
                    double close = getYAxis().getDisplayPosition(extra.getClose());
                    double high = getYAxis().getDisplayPosition(extra.getHigh());
                    double low = getYAxis().getDisplayPosition(extra.getLow());

                    // calculate candle width
                    double candleWidth = -1;
                    if (getXAxis() instanceof DateAxis) {
                    	DateAxis xa = (DateAxis) getXAxis();
                        candleWidth =  xa.getTickLength() * 0.90; // use 90% width between ticks
                    }
                    // update candle
                    node.update(x, close - y, open - y, high - y, low - y, candleWidth);
                    //node.updateTooltip(item.getYValue().doubleValue(), extra.getClose(), extra.getHigh(), extra.getLow());

                    // position the candle
                    node.setLayoutX(x);
                    node.setLayoutY(y);
                }
            }
        }
    }

    @Override
    protected void dataItemChanged(Data<Date, Number> item) {
    }

    @Override
    protected void dataItemAdded(Series<Date, Number> series, int itemIndex, Data<Date, Number> item) {
        Node node = item.getNode(); //createNode(getData().indexOf(series), item, itemIndex);
        if (shouldAnimate()) {
            node.setOpacity(0);
            getPlotChildren().add(node);
            // fade in new candle
            FadeTransition ft = new FadeTransition(Duration.millis(500), node);
            ft.setToValue(1);
            ft.play();
        } else {
            getPlotChildren().add(node);
        }
        // always draw average line on top
        if (series.getNode() != null) {
            series.getNode().toFront();
        }
    }

    @Override
    protected void dataItemRemoved(Data<Date, Number> item, Series<Date, Number> series) {
        final Node candle = item.getNode();
        if (shouldAnimate()) {
            // fade out old candle
            FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
            ft.setToValue(0);
            ft.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    getPlotChildren().remove(candle);
                }
            });
            ft.play();
        } else {
            getPlotChildren().remove(candle);
        }
    }

    @Override
    protected void seriesAdded(Series<Date, Number> series, int seriesIndex) {
        // handle any data already in series
        for (int j = 0; j < series.getData().size(); j++) {
            Data<Date, Number> item = series.getData().get(j);
            Node node = item.getNode(); //createNode(seriesIndex, item, j);
            if (shouldAnimate()) {
                node.setOpacity(0);
                getPlotChildren().add(node);
                // fade in new candle
                FadeTransition ft = new FadeTransition(Duration.millis(500), node);
                ft.setToValue(1);
                ft.play();
            } else {
                getPlotChildren().add(node);
            }
        }
    }

    @Override
    protected void seriesRemoved(Series<Date, Number> series) {
        // remove all candle nodes
        for (XYChart.Data<Date, Number> d : series.getData()) {
            final Node candle = d.getNode();
            if (shouldAnimate()) {
                // fade out old candle
                FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
                ft.setToValue(0);
                ft.setOnFinished(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent actionEvent) {
                        getPlotChildren().remove(candle);
                    }
                });
                ft.play();
            } else {
                getPlotChildren().remove(candle);
            }
        }
    }

    /**
     * This is called when the range has been invalidated and we need to update it. If the axis are auto
     * ranging then we compile a list of all data that the given axis has to plot and call invalidateRange() on the
     * axis passing it that data.
     */
    @Override
    protected void updateAxisRange() {
        // For candle stick chart we need to override this method as we need to let the axis know that they need to be able
        // to cover the whole area occupied by the high to low range not just its center data value
        final Axis<Date> xa = getXAxis();
        final Axis<Number> ya = getYAxis();
        List<Date> xData = null;
        List<Number> yData = null;
        if (xa.isAutoRanging()) {
            xData = new ArrayList<Date>();
        }
        if (ya.isAutoRanging()) {
            yData = new ArrayList<Number>();
        }
        if (xData != null || yData != null) {
            for (Series<Date, Number> series : getData()) {
                for (Data<Date, Number> data : series.getData()) {
                    if (xData != null) {
                        xData.add(data.getXValue());
                    }
                    if (yData != null) {
                        Bar extras = (Bar) data.getExtraValue();
                        if (extras != null) {
                            yData.add(extras.getHigh());
                            yData.add(extras.getLow());
                        } else {
                            yData.add(data.getYValue());
                        }
                    }
                }
            }
            if (xData != null) {
                xa.invalidateRange(xData);
            }
            if (yData != null) {
                ya.invalidateRange(yData);
            }
        }
    }

    public static class BarStick extends Group {
    	private Line highLowLine = new Line();
    	private Line openLine = new Line();
    	private Line closeLine = new Line();
//    	private Region bar = new Region();
//    	private String seriesStyleClass;
//    	private String dataStyleClass;
    	private boolean openAboveClose = true;
//    	private Tooltip tooltip = new Tooltip();

    	public BarStick(String seriesStyleClass, String dataStyleClass) {
    		setAutoSizeChildren(false);
    		getChildren().addAll(highLowLine
    				, openLine
    				, closeLine
    				);
//    		this.seriesStyleClass = seriesStyleClass;
//    		this.dataStyleClass = dataStyleClass;
//    		updateStyleClasses();
//    		tooltip.setGraphic(new TooltipContent());
//    		Tooltip.install(highLowLine, tooltip);
    	}

//    	public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass) {
//    		this.seriesStyleClass = seriesStyleClass;
//    		this.dataStyleClass = dataStyleClass;
//    		updateStyleClasses();
//    	}

    	public void update(double timeOffset, double closeOffset, double openOffset, double highOffset, double lowOffset, double candleWidth) {
    		openAboveClose = closeOffset > 0;
//    		updateStyleClasses();
    		String styleClass = (openAboveClose)?"open-above-close":"close-above-open";
    		highLowLine.setStartY(highOffset);
    		highLowLine.setStartX(timeOffset);
    		highLowLine.setEndX(timeOffset);
    		highLowLine.setEndY(lowOffset);
    		highLowLine.getStyleClass().add(styleClass);

    		openLine.setStartY(openOffset);
    		openLine.setEndY(openOffset);
    		openLine.setStartX(timeOffset);
    		openLine.setEndX(timeOffset-candleWidth/2);
    		openLine.getStyleClass().add(styleClass);

    		closeLine.setStartY(closeOffset);
    		closeLine.setStartX(timeOffset);
    		closeLine.setEndY(closeOffset);
    		closeLine.setEndX(timeOffset+candleWidth/2);
    		closeLine.getStyleClass().add(styleClass);

    	}

//    	public void updateTooltip(double open, double close, double high, double low) {
//    		TooltipContent tooltipContent = (TooltipContent) tooltip.getGraphic();
//    		tooltipContent.update(open, close, high, low);
//          tooltip.setText("Open: "+open+"\nClose: "+close+"\nHigh: "+high+"\nLow: "+low);
//    	}

//    	private void updateStyleClasses() {
//    		getStyleClass().setAll("candlestick-candle", seriesStyleClass, dataStyleClass);
//    		highLowLine.getStyleClass().setAll("candlestick-line", seriesStyleClass, dataStyleClass, openAboveClose ? "open-above-close" : "close-above-open");
//    		bar.getStyleClass().setAll("candlestick-bar", seriesStyleClass, dataStyleClass,
//    				openAboveClose ? "open-above-close" : "close-above-open");
//    	}
    }


    public static class TooltipContent extends GridPane {
    	private Label openValue = new Label();
    	private Label closeValue = new Label();
    	private Label highValue = new Label();
    	private Label lowValue = new Label();

    	TooltipContent() {
    		Label open = new Label("OPEN:");
    		Label close = new Label("CLOSE:");
    		Label high = new Label("HIGH:");
    		Label low = new Label("LOW:");
    		open.getStyleClass().add("candlestick-tooltip-label");
    		close.getStyleClass().add("candlestick-tooltip-label");
    		high.getStyleClass().add("candlestick-tooltip-label");
    		low.getStyleClass().add("candlestick-tooltip-label");
    		setConstraints(open, 0, 0);
    		setConstraints(openValue, 1, 0);
    		setConstraints(close, 0, 1);
    		setConstraints(closeValue, 1, 1);
    		setConstraints(high, 0, 2);
    		setConstraints(highValue, 1, 2);
    		setConstraints(low, 0, 3);
    		setConstraints(lowValue, 1, 3);
    		getChildren().addAll(open, openValue, close, closeValue, high, highValue, low, lowValue);
    	}

    	public void update(double open, double close, double high, double low) {
    		openValue.setText(Double.toString(open));
    		closeValue.setText(Double.toString(close));
    		highValue.setText(Double.toString(high));
    		lowValue.setText(Double.toString(low));
    	}
    }


//    public static class CandleStickExtraValues {
//    	private final double close;
//    	private final double high;
//    	private final double low;
//    	private final double open;
//
//    	public CandleStickExtraValues(Bar b){
//    		this(b.open, b.high, b.low, b.close);
//    	}
//
//    	public CandleStickExtraValues(double open, double high, double low, double close) {
//    		this.close = close;
//    		this.high = high;
//    		this.low = low;
//    		this.open = open;
//    	}
//
//    	public double getClose() {
//    		return close;
//    	}
//
//    	public double getHigh() {
//    		return high;
//    	}
//
//    	public double getLow() {
//    		return low;
//    	}
//
//    	public double getOpen() {
//    		return open;
//    	}
//    }
}



