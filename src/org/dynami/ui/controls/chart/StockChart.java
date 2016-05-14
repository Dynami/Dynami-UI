/*
 * Copyright 2016 Alessandro Atria - a.atria@gmail.com
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dynami.core.data.Bar;
import org.dynami.core.plot.Plot;
import org.dynami.core.utils.DUtils;

import javafx.animation.FadeTransition;
import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.css.StyleableProperty;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.util.Duration;


public class StockChart extends XYChart<Date, Number> {
	private final Map<String, Plot> seriesFormat = new TreeMap<>();
	
	public void setPlotFormat(String seriesName, Plot plot){
		seriesFormat.put(seriesName, plot);
	}
	
	public StockChart(@NamedArg("xAxis") Axis<Date> xAxis, @NamedArg("yAxis") Axis<Number> yAxis) {
		super(xAxis, yAxis);
		setup();
	}
	
	public StockChart(@NamedArg("xAxis") Axis<Date> xAxis, @NamedArg("yAxis") Axis<Number> yAxis, @NamedArg("data") ObservableList<Series<Date, Number>> data) {
		super(xAxis, yAxis);
		setData(data);
		setup();
	}
	
	private void setup(){
		setAnimated(false);	
		getXAxis().setAnimated(false);
        getYAxis().setAnimated(false);

        getStylesheets().add("org/dynami/ui/controls/chart/barstick-stylesheet.css");
	}

	@Override
	protected void dataItemAdded(XYChart.Series<Date, Number> series, int itemIndex, XYChart.Data<Date, Number> item) {
		Node node = createNode(series.getName(), getData().indexOf(series), item, itemIndex);
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
	
	private Node createNode(String seriesName, int seriesIndex, final XYChart.Data<Date, Number> item, int itemIndex) {
		Node node = item.getNode();
		if(node instanceof BarStick || node instanceof Path){
			return node;
		} else if(item.getExtraValue() != null){
			BarStick bar = new BarStick();
			item.setNode(bar);
			return item.getNode();
		} else {
			Segment segment = new Segment(seriesFormat.get(seriesName));
			item.setNode(segment);
			return item.getNode();
		}
	}

	@Override
	protected void dataItemRemoved(XYChart.Data<Date, Number> item, XYChart.Series<Date, Number> series) {
		final Node node = item.getNode();
        if (shouldAnimate()) {
            // fade out old candle
            FadeTransition ft = new FadeTransition(Duration.millis(500), node);
            ft.setToValue(0);
            ft.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    getPlotChildren().remove(node);
                }
            });
            ft.play();
        } else {
            getPlotChildren().remove(node);
        }
	}

	@Override
	protected void dataItemChanged(XYChart.Data<Date, Number> item) {}

	@Override
	protected void seriesAdded(XYChart.Series<Date, Number> series, int seriesIndex) {
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
	protected void seriesRemoved(XYChart.Series<Date, Number> series) {
		// remove all candle nodes
        for (XYChart.Data<Date, Number> d : series.getData()) {
            final Node node = d.getNode();
            if (shouldAnimate()) {
                // fade out old candle
                FadeTransition ft = new FadeTransition(Duration.millis(500), node);
                ft.setToValue(0);
                ft.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        getPlotChildren().remove(node);
                    }
                });
                ft.play();
            } else {
                getPlotChildren().remove(node);
            }
        }
	}

	@Override
	protected void layoutPlotChildren() {
		if (getData() == null) {
            return;
        }
		for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
			final Series<Date, Number> series = getData().get(seriesIndex);
			final Iterator<XYChart.Data<Date, Number>> iter = getDisplayedDataIterator(series);
			XYChart.Data<Date, Number> prev = null, item = null;
			Node itemNode;
			while(iter.hasNext()){
				item = iter.next();
				itemNode = item.getNode();
//				if(Double.isNaN((double)item.getYValue())){
//					continue;
//				}
//				System.out.println("StockChart.layoutPlotChildren() "+series.getName()+" > "+item.getYValue());
				
				double x = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(item));
                double y = getYAxis().getDisplayPosition(getCurrentDisplayedYValue(item));
                if(itemNode instanceof BarStick){
                	BarStick node = (BarStick) itemNode;
                    Bar bar = (Bar)item.getExtraValue();

                    double open = getYAxis().getDisplayPosition(bar.getOpen());
                    double close = getYAxis().getDisplayPosition(bar.getClose());
                    double high = getYAxis().getDisplayPosition(bar.getHigh());
                    double low = getYAxis().getDisplayPosition(bar.getLow());

                    // calculate candle width
                    double candleWidth =  getXAxis().getTickLength() * 0.90; // use 90% width between ticks

                    node.update((bar.close>=bar.open), x, close - y, open - y, high - y, low - y, candleWidth);
                    node.updateTooltip(bar);
                    node.setLayoutY(y);

                } else if(itemNode instanceof Segment){
                	
                    if(prev != null){
                    	double currY = getYAxis().getDisplayPosition(item.getYValue());
                    	double prevY = getYAxis().getDisplayPosition(prev.getYValue());
                    	
                    	double prevX = getXAxis().getDisplayPosition(getCurrentDisplayedXValue(prev));
                    	Plot format = seriesFormat.get(series.getName());
                		
                    	Segment s = (Segment)itemNode;
                		if(format != null){
                			format.color().toString();
                		}
                    	s.update(prevX, x, prevY-y, currY-y);
                		s.setLayoutY(y);
                		
                    }
                } else {
                	System.out.println("StockChart.layoutPlotChildren() Something wrong happend");
                }
                if(item.getExtraValue() == null){
                	if (item.getYValue().doubleValue() == 0.0) {
                		prev = null;
                	}  else {
                		prev = item;
                	}
                }
			}
		}
	}
	
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

	public static class Segment extends Group {
    	private Line line = new Line();
		public Segment(Plot format){
    		setAutoSizeChildren(false);
    		getChildren().addAll(line);
    		setFormat(format);
//    		line.getStrokeDashArray().addAll(25d, 10d);
    		
//    		tooltip.setGraphic(new TooltipContent());
//    		Tooltip.install(this, tooltip);
    	}
    	
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public void setFormat(Plot format){
    		if(format != null){
    			Color c = Color.web(format.color());
    			((StyleableProperty)line.strokeProperty()).applyStyle(null, c);
    		} else {
    			((StyleableProperty)line.strokeProperty()).applyStyle(null, Color.BLACK);
    		}
    	}
    	
    	public void update(double time0, double time1, double value0, double value1){
    		line.setStartX(time0);
    		line.setStartY(value0);
    		
    		line.setEndX(time1);
    		line.setEndY(value1);
//    		line.setStyle("-fx-stroke:"+color+";");
    		
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
