package org.dynami.ui.controls.config;

import javafx.beans.NamedArg;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.converter.LongStringConverter;

public class LongSpinnerValueFactory extends SpinnerValueFactory<Long> {

    /***********************************************************************
     *                                                                     *
     * Constructors                                                        *
     *                                                                     *
     **********************************************************************/

    /**
     * Constructs a new IntegerSpinnerValueFactory that sets the initial value
     * to be equal to the min value, and a default {@code amountToStepBy} of one.
     *
     * @param min The minimum allowed integer value for the Spinner.
     * @param max The maximum allowed integer value for the Spinner.
     */
    public LongSpinnerValueFactory(@NamedArg("min") long min,
                                      @NamedArg("max") long max) {
        this(min, max, min);
    }

    /**
     * Constructs a new IntegerSpinnerValueFactory with a default
     * {@code amountToStepBy} of one.
     *
     * @param min The minimum allowed integer value for the Spinner.
     * @param max The maximum allowed integer value for the Spinner.
     * @param initialValue The value of the Spinner when first instantiated, must
     *                     be within the bounds of the min and max arguments, or
     *                     else the min value will be used.
     */
    public LongSpinnerValueFactory(@NamedArg("min") long min,
                                      @NamedArg("max") long max,
                                      @NamedArg("initialValue") long initialValue) {
        this(min, max, initialValue, 1L);
    }

    /**
     * Constructs a new IntegerSpinnerValueFactory.
     *
     * @param min The minimum allowed integer value for the Spinner.
     * @param max The maximum allowed integer value for the Spinner.
     * @param initialValue The value of the Spinner when first instantiated, must
     *                     be within the bounds of the min and max arguments, or
     *                     else the min value will be used.
     * @param amountToStepBy The amount to increment or decrement by, per step.
     */
    public LongSpinnerValueFactory(@NamedArg("min") long min,
                                      @NamedArg("max") long max,
                                      @NamedArg("initialValue") long initialValue,
                                      @NamedArg("amountToStepBy") long amountToStepBy) {
        setMin(min);
        setMax(max);
        setAmountToStepBy(amountToStepBy);
        setConverter(new LongStringConverter());

        valueProperty().addListener((o, oldValue, newValue) -> {
            // when the value is set, we need to react to ensure it is a
            // valid value (and if not, blow up appropriately)
            if (newValue < getMin()) {
                setValue(getMin());
            } else if (newValue > getMax()) {
                setValue(getMax());
            }
        });
        setValue(initialValue >= min && initialValue <= max ? initialValue : min);
    }


    /***********************************************************************
     *                                                                     *
     * Properties                                                          *
     *                                                                     *
     **********************************************************************/

    // --- min
    private LongProperty min = new SimpleLongProperty(this, "min") {
        @Override protected void invalidated() {
            Long currentValue = LongSpinnerValueFactory.this.getValue();
            if (currentValue == null) {
                return;
            }

            long newMin = get();
            if (newMin > getMax()) {
                setMin(getMax());
                return;
            }

            if (currentValue < newMin) {
                LongSpinnerValueFactory.this.setValue(newMin);
            }
        }
    };

    public final void setMin(long value) {
        min.set(value);
    }
    public final long getMin() {
        return min.get();
    }
    /**
     * Sets the minimum allowable value for this value factory
     */
    public final LongProperty minProperty() {
        return min;
    }

    // --- max
    private LongProperty max = new SimpleLongProperty(this, "max") {
        @Override protected void invalidated() {
            Long currentValue = LongSpinnerValueFactory.this.getValue();
            if (currentValue == null) {
                return;
            }

            long newMax = get();
            if (newMax < getMin()) {
                setMax(getMin());
                return;
            }

            if (currentValue > newMax) {
                LongSpinnerValueFactory.this.setValue(newMax);
            }
        }
    };

    public final void setMax(long value) {
        max.set(value);
    }
    public final long getMax() {
        return max.get();
    }
    /**
     * Sets the maximum allowable value for this value factory
     */
    public final LongProperty maxProperty() {
        return max;
    }

    // --- amountToStepBy
    private LongProperty amountToStepBy = new SimpleLongProperty(this, "amountToStepBy");
    public final void setAmountToStepBy(long value) {
        amountToStepBy.set(value);
    }
    public final long getAmountToStepBy() {
        return amountToStepBy.get();
    }
    /**
     * Sets the amount to increment or decrement by, per step.
     */
    public final LongProperty amountToStepByProperty() {
        return amountToStepBy;
    }



    /***********************************************************************
     *                                                                     *
     * Overridden methods                                                  *
     *                                                                     *
     **********************************************************************/

    /** {@inheritDoc} */
    @Override public void decrement(int steps) {
        final long min = getMin();
        final long max = getMax();
        final long newIndex = getValue() - steps * getAmountToStepBy();
        setValue(newIndex >= min ? newIndex : (isWrapAround() ? wrapValue(newIndex, min, max) + 1 : min));
    }

    /** {@inheritDoc} */
    @Override public void increment(int steps) {
        final long min = getMin();
        final long max = getMax();
        final long currentValue = getValue();
        final long newIndex = currentValue + steps * getAmountToStepBy();
        setValue(newIndex <= max ? newIndex : (isWrapAround() ? wrapValue(newIndex, min, max) - 1 : max));
    }
    
    static long wrapValue(long value, long min, long max) {
        if (max == 0) {
            throw new RuntimeException();
        }

        long r = value % max;
        if (r > min && max < min) {
            r = r + max - min;
        } else if (r < min && max > min) {
            r = r + max - min;
        }
        return r;
    }
}