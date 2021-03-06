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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class CandleStickChart extends XYChart<Date, Number> {
    /**
     * Construct a new CandleStickChart with the given axis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public CandleStickChart(@NamedArg("xAxis") Axis<Date> xAxis, @NamedArg("yAxis") Axis<Number> yAxis) {
        super(xAxis, yAxis);
        setAnimated(false);
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);

        getStylesheets().add("org/dynami/ui/controls/chart/candlestick-stylesheet.css");
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("org/dynami/ui/controls/charts/candle_stick_chart.fxml"));
//        fxmlLoader.setRoot(this);
//        fxmlLoader.setController(this);
//		try {
//			fxmlLoader.load();
//		} catch (IOException exception) {
//			throw new RuntimeException(exception);
//		}
    }

    /**
     * Construct a new CandleStickChart with the given axis and data.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @param data The data to use, this is the actual list used so any changes to it will be reflected in the chart
     */
    public CandleStickChart(@NamedArg("xAxis") Axis<Date> xAxis, @NamedArg("yAxis") Axis<Number> yAxis, @NamedArg("data") ObservableList<Series<Date, Number>> data) {
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
//            Path seriesPath = null;
//            if (series.getNode() instanceof Path) {
//                seriesPath = (Path) series.getNode();
//                seriesPath.getElements().clear();
//            }
            while (iter.hasNext()) {
                Data<Date, Number> item = iter.next();
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                Node itemNode = item.getNode();
                Bar extra = (Bar) item.getExtraValue();
                if (itemNode instanceof Candle && extra != null) {
                    Candle candle = (Candle) itemNode;
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
                    candle.update(close - y, high - y, low - y, candleWidth);
                    candle.updateTooltip(item.getYValue().doubleValue(), extra.getClose(), extra.getHigh(), extra.getLow());

                    // position the candle
                    candle.setLayoutX(x);
                    candle.setLayoutY(y);
                }
//                if (seriesPath != null) {
//                    if (seriesPath.getElements().isEmpty()) {
//                        seriesPath.getElements().add(new MoveTo(x, getYAxis().getDisplayPosition(extra.getAverage())));
//                    } else {
//                        seriesPath.getElements().add(new LineTo(x, getYAxis().getDisplayPosition(extra.getAverage())));
//                    }
//                }
            }
        }
    }

    @Override
    protected void dataItemChanged(Data<Date, Number> item) {
    }

    @Override
    protected void dataItemAdded(Series<Date, Number> series, int itemIndex, Data<Date, Number> item) {
        Node candle = createCandle(getData().indexOf(series), item, itemIndex);
        if (shouldAnimate()) {
            candle.setOpacity(0);
            getPlotChildren().add(candle);
            // fade in new candle
            FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
            ft.setToValue(1);
            ft.play();
        } else {
            getPlotChildren().add(candle);
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
            Node candle = createCandle(seriesIndex, item, j);
            if (shouldAnimate()) {
                candle.setOpacity(0);
                getPlotChildren().add(candle);
                // fade in new candle
                FadeTransition ft = new FadeTransition(Duration.millis(500), candle);
                ft.setToValue(1);
                ft.play();
            } else {
                getPlotChildren().add(candle);
            }
        }
        // create series path
//        Path seriesPath = new Path();
//        seriesPath.getStyleClass().setAll("candlestick-average-line", "series" + seriesIndex);
//        series.setNode(seriesPath);
//        getPlotChildren().add(seriesPath);
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
     * Create a new Candle node to represent a single data item
     *
     * @param seriesIndex The index of the series the data item is in
     * @param item        The data item to create node for
     * @param itemIndex   The index of the data item in the series
     * @return New candle node to represent the give data item
     */
    private Node createCandle(int seriesIndex, final Data<Date, Number> item, int itemIndex) {
        Node candle = item.getNode();
        // check if candle has already been created
        if (candle instanceof Candle) {
            ((Candle) candle).setSeriesAndDataStyleClasses("series" + seriesIndex, "data" + itemIndex);
        } else {
            candle = new Candle("series" + seriesIndex, "data" + itemIndex);
            item.setNode(candle);
        }
        return candle;
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

    public static class Candle extends Group {
    	private Line highLowLine = new Line();
    	private Region bar = new Region();
    	private String seriesStyleClass;
    	private String dataStyleClass;
    	private boolean openAboveClose = true;
    	private Tooltip tooltip = new Tooltip();

    	public Candle(String seriesStyleClass, String dataStyleClass) {
    		setAutoSizeChildren(false);
    		getChildren().addAll(highLowLine, bar);
    		this.seriesStyleClass = seriesStyleClass;
    		this.dataStyleClass = dataStyleClass;
    		updateStyleClasses();
    		tooltip.setGraphic(new TooltipContent());
    		Tooltip.install(bar, tooltip);
    	}

    	public void setSeriesAndDataStyleClasses(String seriesStyleClass, String dataStyleClass) {
    		this.seriesStyleClass = seriesStyleClass;
    		this.dataStyleClass = dataStyleClass;
    		updateStyleClasses();
    	}

    	public void update(double closeOffset, double highOffset, double lowOffset, double candleWidth) {
    		openAboveClose = closeOffset > 0;
    		updateStyleClasses();
    		highLowLine.setStartY(highOffset);
    		highLowLine.setEndY(lowOffset);
    		if (candleWidth == -1) {
    			candleWidth = bar.prefWidth(-1);
    		}
    		if (openAboveClose) {
    			bar.resizeRelocate(-candleWidth / 2, 0, candleWidth, closeOffset);
    		} else {
    			bar.resizeRelocate(-candleWidth / 2, closeOffset, candleWidth, closeOffset * -1);
    		}
    	}

    	public void updateTooltip(double open, double close, double high, double low) {
    		TooltipContent tooltipContent = (TooltipContent) tooltip.getGraphic();
    		tooltipContent.update(open, close, high, low);
//                tooltip.setText("Open: "+open+"\nClose: "+close+"\nHigh: "+high+"\nLow: "+low);
    	}

    	private void updateStyleClasses() {
    		getStyleClass().setAll("candlestick-candle", seriesStyleClass, dataStyleClass);
    		highLowLine.getStyleClass().setAll("candlestick-line", seriesStyleClass, dataStyleClass,
    				openAboveClose ? "open-above-close" : "close-above-open");
    		bar.getStyleClass().setAll("candlestick-bar", seriesStyleClass, dataStyleClass,
    				openAboveClose ? "open-above-close" : "close-above-open");
    	}
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



