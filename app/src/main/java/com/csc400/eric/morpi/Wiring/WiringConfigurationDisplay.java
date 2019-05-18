package com.csc400.eric.morpi.Wiring;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.csc400.eric.morpi.ProjectConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WiringConfigurationDisplay extends View
{
    /** Flag to check whether to draw wire numbers */
    private boolean drawWireNums;
    /** Flag to indicate whether start drawing
     * the wire from the center of the arc. */
    private boolean useArcCenter = false;

    /** Exponent for distance formula */
    private int distFormulaExponent = 2;

    /** Stroke width for wire and pinhole outlines.*/
    private int outlineStrokeWidth2dp = 2;
    /** Stroke width for wires.*/
    private int wireStrokeWidth12dp = 12;

    /** Padding in 5dp */
    private int padding5dp = 5;
    /** Padding in 12dp */
    private int padding12dp = 12;
    /** Padding in 40dp */
    private int padding40dp = 40;

    /** 175 degree angle */
    private int degrees175 = 175;
    /** 180 degree angle */
    private int degrees180 = 180;
    /** 190 degree angle */
    private int degrees190 = 190;
    /** Text Color and Black wire color */

    private int black = 0xFF000000;
    /** Color of the current wire being drawn */
    private int wireColor;
    /** Starting index of the wire color to be drawn */
    private int wireColorLowerIndex = 0;
    /** End index of the wire color to be drawn */
    protected int wireColorUpperIndex = 10;

    /** The X coordinate at which to
     * draw the first end of the wire.
     */
    private float screenStartX = 0;
    /** The Y coordinate at which to
     * draw the first end of the wire.
     */
    private float screenStartY = 0;
    /** The X coordinate at which to
     * draw the second end of the wire.
     */
    private float screenEndX = 0;
    /** The Y coordinate at which to
     * draw the second end of the wire.
     */
    private float screenEndY = 0;

    /** List holding all wire colors */
    private List<Integer> wireColors = new ArrayList<>();

    /** Paint object to draw on canvas */
    private Paint paint = new Paint();

    /**
     * Constructor which accepts a Context.
     *
     * @param context The context to which this view is attached.
     */
    public WiringConfigurationDisplay(Context context)
    {
        super(context);
    }

    /**
     * Constructor which accepts a Context and an AttributeSet.
     *
     * @param context The context to which this view is attached.
     * @param attrs The attributes to set
     */
    public WiringConfigurationDisplay(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Constructor which accepts a Context, an, AttributeSet, and
     * an integer defining the attribute style.
     *
     * @param context The context to which this view is attached.
     * @param attrs The attributes to set
     * @param defStyleAttr The attribute style
     */
    public WiringConfigurationDisplay(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Displays the project
     * wiring configuration.
     */
    public void draw()
    {
        invalidate();
        requestLayout();
    }

    /**
     * Draws the project wiring
     * configuration on the canvas.
     *
     * @param canvas The canvas on which to draw the wires.
     */
    @Override
    public void onDraw(Canvas canvas)
    {
        int radius = 50;

        double degrees;
        double distanceBetweenPoints;

        List<WireEndCoordinates> aPoints;
        List<WireEndCoordinates> bPoints;

        WireEndCoordinates pointA;
        WireEndCoordinates pointB;

        paint.setAntiAlias(true);
        paint.setTextSize(24f);
        paint.setTextAlign(Paint.Align.CENTER);

        ProjectConfig.initializeWireEndpoints();

        for(int i = wireColorLowerIndex; i < wireColorUpperIndex; i+=2)
        {
            aPoints = ProjectConfig.wireEndPointsList[i];
            bPoints = ProjectConfig.wireEndPointsList[i+1];

            wireColor = wireColors.get(i/2);

            for (int j = 0; j < bPoints.size(); j++)
            {
                pointA = aPoints.get(j);
                pointB = bPoints.get(j);

                if(Objects.nonNull(pointA) && Objects.nonNull(pointB))
                {
                    // Pinhole X Coordinates are lined up perfectly Horizontal
                    if(pointA.getWireEndY() == pointB.getWireEndY())
                    {
                        setStartAndEndCoordinates(pointA, pointB, (pointA.getWireEndX() > pointB.getWireEndX()));
                        canvas = drawBlackenedPinholeOnBothEnds(canvas);
                        canvas = drawHorizontallyAlignedWire(canvas, radius);
                    }
                    else
                        // Pinhole Y Coordinates lined up perfectly vertical
                        if (pointA.getWireEndX() == pointB.getWireEndX())
                        {
                            setStartAndEndCoordinates(pointA, pointB, (pointA.getWireEndY() > pointB.getWireEndY()));
                            canvas = drawBlackenedPinholeOnBothEnds(canvas);
                            canvas = drawVerticallyAlignedWire(canvas, radius);
                        }
                        else
                        // Pinholes are diagonal: X Coordinates are not equal and Y Coordinates are not equal.
                        // Wires are curved on an angle.
                        {
                            setStartAndEndCoordinates(pointA, pointB, (pointA.getWireEndX() > pointB.getWireEndX()));
                            distanceBetweenPoints = Math.sqrt(Math.pow((screenEndX - screenStartX), distFormulaExponent) + Math.pow((screenEndY - screenStartY), distFormulaExponent));
                            degrees = computeDegrees();

                            canvas = drawBlackenedPinholeOnBothEnds(canvas);
                            canvas = drawAngledWire(canvas, radius, degrees, distanceBetweenPoints);

                            canvas.restore();
                        }
                }
                else
                if (Objects.isNull(pointA) && Objects.nonNull(pointB))
                {
                    setStartCoordinates(pointB.getWireEndX(), pointB.getWireEndY());
                    canvas = drawOneEndOfWire(canvas);
                }
                else
                if (Objects.isNull(pointB) && Objects.nonNull(pointA))
                {
                    setStartCoordinates(pointA.getWireEndX(), pointA.getWireEndY());
                    canvas = drawOneEndOfWire(canvas);
                }
            }

            if (aPoints.size() > bPoints.size())
            {
                pointA = aPoints.get(aPoints.size()-1);
                setStartCoordinates(pointA.getWireEndX(), pointA.getWireEndY());
                canvas = drawOneEndOfWire(canvas);
            }

            setPaintProperties(Paint.Style.FILL, wireStrokeWidth12dp, black);

            if(drawWireNums)
            {
                canvas = drawWireNumbers(canvas, aPoints);
                canvas = drawWireNumbers(canvas, bPoints);
            }
        }

        super.onDraw(canvas);
    }

    /**
     * Initializes the wireColors list
     * with all available wire colors.
     */
    public void initializeWireColors()
    {
        int white = 0xFFFFFFFF;
        int red = 0xFFFF0000;
        int blue = 0xFF0000FF;
        int yellow = 0xFFFFFF00;

        wireColors.add(white);
        wireColors.add(black);
        wireColors.add(red);
        wireColors.add(blue);
        wireColors.add(yellow);
    }

    /**
     * Allows user to view wires only,
     * or wires and wire numbers.
     *
     * @param withNums boolean value
     */
    public void drawWiringConfigWithNums(boolean withNums)
    {
        drawWireNums = withNums;
        draw();
    }

    /**
     * Allows user to only view wires of a
     * specific color, or wires of all colors.
     *
     * @param lowerIndex int starting index of color in wireColors list
     * @param upperIndex int ending index of color in wireColors list
     */
    public void drawWiresOfSelectedColor(int lowerIndex, int upperIndex)
    {
        wireColorLowerIndex = lowerIndex;
        wireColorUpperIndex = upperIndex;
        draw();
    }

    /**
     * Draws a wire between two pinholes
     * whose X Coordinates are equal.
     *
     * @param canvas the canvas to draw on
     * @param radius int radius of the wire
     *
     * @return the canvas
     */
    private Canvas drawHorizontallyAlignedWire(Canvas canvas, int radius)
    {
        // Draw wire
        setPaintProperties(Paint.Style.STROKE, wireStrokeWidth12dp, wireColor);
        canvas.drawArc(screenStartX, screenStartY - radius, screenEndX, screenEndY + radius, degrees180, degrees180, useArcCenter, paint);

        // Draw wire outline
        setPaintProperties(Paint.Style.STROKE, outlineStrokeWidth2dp, black);
        canvas.drawArc(screenStartX - padding5dp, screenStartY - radius - padding5dp, screenEndX + padding5dp, screenEndY + radius, degrees175, degrees190, useArcCenter, paint);
        canvas.drawArc(screenStartX + padding5dp, screenStartY - radius + padding5dp, screenEndX - padding5dp, screenEndY + radius, degrees180, degrees180, useArcCenter, paint);

        return canvas;
    }

    /**
     * Draws a wire between two pinholes
     * whose Y Coordinates are equal.
     *
     * @param canvas the canvas to draw on
     * @param radius int radius of the wire
     *
     * @return the canvas
     */
    private Canvas drawVerticallyAlignedWire(Canvas canvas, int radius)
    {
        // Width = 1024dp --- Half width = 512dp
        float xOrigin = (float)getWidth()/2;

        // Wire curves on Left
        if (screenStartX < xOrigin)
        {
            canvas = drawLeftVerticallyAlignedWire(canvas, radius);
        }
        // Wire curves on Right
        else
        {
            canvas = drawRightVerticallyAlignedWire(canvas, radius);
        }

        return canvas;
    }

    /**
     * Draws a wire which curves to the left between
     * two pinholes whose Y Coordinates are equal.
     *
     * This function is called when both pinhole X Coordinates
     * are less than half the width of the screen.
     *
     * @param canvas the canvas to draw on
     * @param radius int radius of the wire
     *
     * @return the canvas
     */
    private Canvas drawLeftVerticallyAlignedWire(Canvas canvas, int radius)
    {
        int degrees85 = 85;
        int degrees90 = 90;

        // Draw wire
        setPaintProperties(Paint.Style.STROKE, wireStrokeWidth12dp, wireColor);
        canvas.drawArc(screenStartX - radius, screenStartY, screenEndX + radius, screenEndY, degrees90, degrees180, useArcCenter, paint);

        // Draw wire outline
        setPaintProperties(Paint.Style.STROKE, outlineStrokeWidth2dp, black);
        canvas.drawArc(screenStartX - radius- padding5dp, screenStartY- padding5dp, screenEndX + radius, screenEndY+ padding5dp, degrees85, degrees190, useArcCenter, paint);
        canvas.drawArc(screenStartX - radius+ padding5dp, screenStartY+ padding5dp, screenEndX + radius, screenEndY- padding5dp, degrees90, degrees180, useArcCenter, paint);

        return canvas;
    }

    /**
     * Draws a wire which curves to the right between
     * two pinholes whose Y Coordinates are equal.
     *
     * This function is called when both pinhole X Coordinates
     * are greater than half the width of the screen.
     *
     * @param canvas the canvas to draw on
     * @param radius int radius of the wire
     *
     * @return the canvas
     */
    private Canvas drawRightVerticallyAlignedWire(Canvas canvas, int radius)
    {
        int degrees265 = 265;
        int degrees270 = 270;

        // Draw wire
        setPaintProperties(Paint.Style.STROKE, wireStrokeWidth12dp, wireColor);
        canvas.drawArc(screenStartX - radius, screenStartY, screenEndX + radius, screenEndY, degrees270, degrees180, useArcCenter, paint);

        // Draw wire outline
        setPaintProperties(Paint.Style.STROKE, outlineStrokeWidth2dp, black);
        canvas.drawArc(screenStartX - radius, screenStartY - padding5dp, screenEndX + radius + padding5dp, screenEndY + padding5dp, degrees265, degrees190, useArcCenter, paint);
        canvas.drawArc(screenStartX - radius, screenStartY + padding5dp, screenEndX + radius - padding5dp, screenEndY - padding5dp, degrees270, degrees180, useArcCenter, paint);

        return canvas;
    }

    /**
     * Draws a angled wire between two pinholes which are not vertically
     * or horizontally aligned.
     *
     * @param canvas the canvas to draw on
     * @param radius int radius of the wire
     * @param degrees double angle of the wire in degrees
     * @param distanceBetweenPoints double distance between the two points
     *
     * @return the canvas
     */
    private Canvas drawAngledWire(Canvas canvas, int radius, double degrees, double distanceBetweenPoints)
    {
        int padding20dp = 20;

        // Draw Wire
        setPaintProperties(Paint.Style.STROKE, wireStrokeWidth12dp, wireColor);
        canvas.save();
        canvas.rotate((float)degrees, screenStartX, screenStartY);
        canvas.drawArc(screenStartX, screenStartY - ((float)(radius / 2.0)) - padding20dp, (float)(screenStartX + distanceBetweenPoints), screenStartY + radius, degrees180, degrees180, useArcCenter, paint);

        // Draw wire outline
        setPaintProperties(Paint.Style.STROKE, outlineStrokeWidth2dp, black);
        canvas.drawArc(screenStartX - padding5dp, screenStartY - ((float)(radius / 2.0)) - padding20dp - padding5dp, (float)(screenStartX + distanceBetweenPoints + padding5dp), screenStartY + radius, degrees175, degrees190, useArcCenter, paint);
        canvas.drawArc(screenStartX + padding5dp, screenStartY - ((float)(radius / 2.0)) - padding20dp + padding5dp, (float)(screenStartX + distanceBetweenPoints - padding5dp), screenStartY + radius, degrees180, degrees180, useArcCenter, paint);

        return canvas;
    }

    /**
     * Draws the filled in (blackened) representation of
     * the pinhole to represent that a wire is plugged in.
     *
     * @param canvas the canvas to draw on
     * @return the canvas
     */
    private Canvas drawBlackenedPinholeOnBothEnds(Canvas canvas)
    {
        setPaintProperties(Paint.Style.FILL, wireStrokeWidth12dp, black);
        canvas.drawRect(screenStartX-padding12dp, screenStartY-padding12dp, screenStartX+padding12dp, screenStartY+padding12dp, paint);
        canvas.drawRect(screenEndX-padding12dp, screenEndY-padding12dp, screenEndX+padding12dp, screenEndY+padding12dp, paint);

        return canvas;
    }

    /**
     * Draws a single end of a wire and blackens
     * the pinhole to which it is plugged in.
     *
     * @param canvas the canvas to draw on
     * @return the canvas
     */
    private Canvas drawOneEndOfWire(Canvas canvas)
    {
        int padding1dp = 1;
        int padding6dp = 6;
        int padding41dp = 41;

        // Draw Plug On one wend (black pinhole)
        setPaintProperties(Paint.Style.FILL, wireStrokeWidth12dp, black);
        canvas.drawRect(screenStartX - padding12dp, screenStartY - padding12dp, screenStartX + padding12dp, screenStartY + padding12dp, paint);

        // Draw Wire End Outline
        setPaintProperties(Paint.Style.STROKE, outlineStrokeWidth2dp, black);
        canvas.drawRect(screenStartX - padding6dp, screenStartY - padding41dp, screenStartX + padding6dp, screenStartY + padding1dp, paint);

        // Draw Wire
        setPaintProperties(Paint.Style.FILL, wireStrokeWidth12dp, wireColor);
        canvas.drawRect(screenStartX - padding5dp, screenStartY - padding40dp, screenStartX + padding5dp, screenStartY, paint);

        return canvas;
    }

    /**
     * Draws a wire number beneath each Pinhole to
     * which a corresponding wire end is plugged in.
     *
     * @param canvas the canvas to draw on
     * @param wireEnds list holding the wire ends
     *
     * @return the canvas
     */
    private Canvas drawWireNumbers(Canvas canvas, List<WireEndCoordinates> wireEnds)
    {
        WireEndCoordinates point;

        for (int j = 0; j < wireEnds.size(); j++)
        {
            point = wireEnds.get(j);

            if(Objects.nonNull(point))
            {
                paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
                canvas.drawText(String.valueOf(j + 1), point.getWireEndX(), point.getWireEndY() + padding40dp, paint);
            }
        }
        return canvas;
    }

    /**
     * Computes the angle at which
     * to draw the wire in degrees.
     *
     * @return double angle in degrees
     */
    private double computeDegrees()
    {
        double opp;
        double adj;
        double hyp;
        double degrees;

        opp = Math.abs(screenEndY - screenStartY);
        adj = Math.abs(screenEndX - screenStartX);
        hyp = Math.sqrt(Math.pow(opp, distFormulaExponent) + Math.pow(adj, distFormulaExponent));

        if (screenStartY < screenEndY)
        {
            degrees = Math.toDegrees(Math.asin(opp/hyp));
        }
        else
        {
            degrees = (-Math.toDegrees(Math.asin(opp/hyp)));
        }

        return degrees;
    }

    /**
     * Sets the point at which to start drawing the wire.
     *
     * @param pointA WireEndCoordinates from a point in the aPoints list.
     * @param pointB WireEndCoordinates from a point in the bPoints list.
     * @param startWithB boolean indicating which point to start drawing from
     */
    private void setStartAndEndCoordinates(WireEndCoordinates pointA, WireEndCoordinates pointB, boolean startWithB)
    {
        if(startWithB)
        {
            setStartCoordinates(pointB.getWireEndX(), pointB.getWireEndY());
            setEndCoordinates(pointA.getWireEndX(), pointA.getWireEndY());
        }
        else
        {
            setStartCoordinates(pointA.getWireEndX(), pointA.getWireEndY());
            setEndCoordinates(pointB.getWireEndX(), pointB.getWireEndY());
        }
    }

    /**
     * Sets the start coordinates at which to draw a wire.
     *
     * @param x float starting x coordinate
     * @param y float starting y coordinate
     */
    private void setStartCoordinates(float x, float y)
    {
        screenStartX = x;
        screenStartY = y;
    }

    /**
     * Sets the end coordinates at which to draw a wire.
     *
     * @param x float starting x coordinate
     * @param y float starting y coordinate
     */
    private void setEndCoordinates(float x, float y)
    {
        screenEndX = x;
        screenEndY = y;
    }

    /**
     * Sets the properties of the paint object.
     *
     * @param style Paint style
     * @param width float stroke width
     * @param color int color
     */
    private void setPaintProperties(Paint.Style style, float width, int color)
    {
        paint.setStyle(style);
        paint.setStrokeWidth(width);
        paint.setColor(color);
    }

}
