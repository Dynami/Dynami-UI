/*
 * Copyright 2015 Alessandro Atria - a.atria@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dynami.ui.controls.chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dynami.core.data.Bar;
import org.dynami.core.utils.DUtils;

import javafx.animation.FadeTransition;
import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class BarStickChart extends XYChart<Number, Number> {
    /**
     * Construct a new BarStickChart with the given axis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public BarStickChart(@NamedArg("xAxis") Axis<Number> xAxis, @NamedArg("yAxis") Axis<Number> yAxis) {
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
    public BarStickChart(@NamedArg("xAxis") Axis<Number> xAxis, @NamedArg("yAxis") Axis<Number> yAxis, @NamedArg("data") ObservableList<Series<Number, Number>> data) {
        this(xAxis, yAxis);
        setData(data);
    }

    /** Called to update and layout the content for the plot */
    @Override
    protected void layoutPlotChildren() {
        // we have nothing to layout if no data is present
        if (getData() == null) {
            return;
        }
        // update candle positions
        for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
            Series<Number, Number> series = getData().get(seriesIndex);
            Iterator<Data<Number, Number>> iter = getDisplayedDataIterator(series);
            Data<Number, Number> prev = null;
            while (iter.hasNext()) {
                Data<Number, Number> item = iter.next();
                double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                Node itemNode = item.getNode();
                Object extra = item.getExtraValue();
                if (itemNode instanceof BarStick && extra != null) {
                    BarStick node = (BarStick) itemNode;
                    Bar bar = (Bar)extra;

                    double open = getYAxis().getDisplayPosition(bar.getOpen());
                    double close = getYAxis().getDisplayPosition(bar.getClose());
                    double high = getYAxis().getDisplayPosition(bar.getHigh());
                    double low = getYAxis().getDisplayPosition(bar.getLow());

                    // calculate candle width
                    double candleWidth =  getXAxis().getTickLength() * 0.90; // use 90% width between ticks

                    node.update((bar.close>=bar.open), x, close - y, open - y, high - y, low - y, candleWidth);
                    node.updateTooltip(bar);
//                   node.setLayoutX(x);
                    node.setLayoutY(y);
                } else {
                	if(prev != null){
                		double value0 = getYAxis().getDisplayPosition(prev.getYValue());
                        double value1 = getYAxis().getDisplayPosition(item.getYValue());
                        
                        double time0 = getXAxis().getDisplayPosition(prev.getXValue());
                        double time1 = getXAxis().getDisplayPosition(item.getXValue());
                		Segment s = new Segment();
                		s.update(time0, time1, value0, value1);
                		s.setLayoutY(y);
                	}
                	prev = item;
                }
            }
        }
    }

    @Override
    protected void dataItemChanged(Data<Number, Number> item) {
    }

    private Node createNode(int seriesIndex, final Data<Number, Number> _item, int itemIndex) {
        Node node = _item.getNode();
        Object extra = _item.getExtraValue();
        if(node instanceof BarStick){
        } else if(extra != null){
        	_item.setNode(new BarStick());
        } else {
        	_item.setNode(new Segment());
        }
        // check if candle has already been created
//        if (stick instanceof BarStick) {
//            //((BarStick) candle).setSeriesAndDataStyleClasses("series" + seriesIndex, "data" + itemIndex);
//        } else if (stick instanceof Segment) {
//        	// do
//        } else {
//            stick = new BarStick();
//            _item.setNode(stick);
//        }
        return _item.getNode();
    }

    @Override
    protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
        Node node = createNode(getData().indexOf(series), item, itemIndex);
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

    @Override
    protected void dataItemRemoved(Data<Number, Number> item, Series<Number, Number> series) {
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
    protected void seriesAdded(Series<Number, Number> series, int seriesIndex) {
        // handle any data already in series
        for (int j = 0; j < series.getData().size(); j++) {
            Data<Number, Number> item = series.getData().get(j);
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
    protected void seriesRemoved(Series<Number, Number> series) {
        // remove all candle nodes
        for (XYChart.Data<Number, Number> d : series.getData()) {
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
        final Axis<Number> xa = getXAxis();
        final Axis<Number> ya = getYAxis();
        List<Number> xData = null;
        List<Number> yData = null;
        if (xa.isAutoRanging()) {
            xData = new ArrayList<Number>();
        }
        if (ya.isAutoRanging()) {
            yData = new ArrayList<Number>();
        }
        if (xData != null || yData != null) {
            for (Series<Number, Number> series : getData()) {
                for (Data<Number, Number> data : series.getData()) {
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
    
    public static class Segment extends Group {
    	private Line line = new Line();
    	public Segment(){
    		setAutoSizeChildren(false);
    		getChildren().addAll(line);
//    		tooltip.setGraphic(new TooltipContent());
//    		Tooltip.install(this, tooltip);
    	}
    	
    	public void update(double time0, double time1, double value0, double value1){
    		line.setStartX(time0);
    		line.setStartY(value0);
    		
    		line.setEndX(time1);
    		line.setEndY(value1);
    	}
    }

    public static class BarStick extends Group {
    	private Line highLowLine = new Line();
    	private Line openLine = new Line();
    	private Line closeLine = new Line();
    	private Tooltip tooltip = new Tooltip();

    	public BarStick() {
    		setAutoSizeChildren(false);
    		getChildren().addAll(highLowLine
    				, openLine
    				, closeLine
    				);
    		tooltip.setGraphic(new TooltipContent());
    		Tooltip.install(this, tooltip);
    	}


    	public void update(boolean closeAboveOpen, double timeOffset, double closeOffset, double openOffset, double highOffset, double lowOffset, double candleWidth) {
    		String styleClass = (closeAboveOpen)?"close-above-open":"open-above-close";

    		highLowLine.setStartX(timeOffset);
    		highLowLine.setEndX(timeOffset);
    		highLowLine.setStartY(highOffset);
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

    	public void updateTooltip(Bar bar) {
    		TooltipContent tooltipContent = (TooltipContent)tooltip.getGraphic();
    		tooltipContent.update(bar);
    	}
    }

    public static class TooltipContent extends GridPane {
    	private Label timeValue = new Label();
    	private Label openValue = new Label();
    	private Label closeValue = new Label();
    	private Label highValue = new Label();
    	private Label lowValue = new Label();

    	TooltipContent() {
    		Label open = new Label("OPEN:");
    		Label close = new Label("CLOSE:");
    		Label high = new Label("HIGH:");
    		Label low = new Label("LOW:");
//    		time.getStyleClass().add("candlestick-tooltip-label");
    		open.getStyleClass().add("candlestick-tooltip-label");
    		close.getStyleClass().add("candlestick-tooltip-label");
    		high.getStyleClass().add("candlestick-tooltip-label");
    		low.getStyleClass().add("candlestick-tooltip-label");
    		setConstraints(timeValue, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, new Insets(2));

    		setConstraints(open, 0, 1);
    		setConstraints(openValue, 1, 1, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, new Insets(2));

    		setConstraints(close, 0, 2);
    		setConstraints(closeValue, 1, 2, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, new Insets(2));

    		setConstraints(high, 0, 3);
    		setConstraints(highValue, 1, 3, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, new Insets(2));

    		setConstraints(low, 0, 4);
    		setConstraints(lowValue, 1, 4, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS, new Insets(2));

    		getChildren().addAll(timeValue, open, openValue, close, closeValue, high, highValue, low, lowValue);
    	}

    	public void update(Bar bar) {
    		timeValue.setText(DUtils.LONG_DATE_FORMAT.format(bar.time));
    		openValue.setText(Double.toString(bar.open));
    		closeValue.setText(Double.toString(bar.close));
    		highValue.setText(Double.toString(bar.high));
    		lowValue.setText(Double.toString(bar.low));
    	}
    }
}



