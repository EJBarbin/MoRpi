package com.csc400.eric.morpi.Breadboard;

import android.util.DisplayMetrics;

public class BreadboardUtil
{
    /** Used to convert dp values to sp values */
    private static DisplayMetrics displayMetrics;

    /** Pinhole width and height in DP */
    private static int widthAndHeightOfPinhole = 20;
    /** Cumulative width of pinhole and whitespace
     * to the right of each pinhole in dp */
    private static int widthOfPinholeXBucket = 28;
    /** Cumulative height of pinhole and whitespace
     * underneath each pinhole in dp */
    private static int heightOfPinholeYBucket = 40;

    // Y positioning hardware constraints
    /** Space between the app header and the (4 x 20)
     * pinhole layouts in DP */
    private static int yPadding4x20PinholeLayout = 14;
    /** Minimum y coordinate where a hardware
     * component may be placed. */
    private static int yMinimumHardware = 814;
    /** Maximum x coordinate where a hardware component
     * may be rotated from horizontal to vertical orientation*/
    public static int yCutoffHardwareRotated = 1212; // should be 1214 but resistor still rotates at second to last pinhole

    // Left positioning hardware constraints
    /** Minimum x coordinate where a hardware
     * component may be placed on the left
     * pinhole layout. */
    public static int leftPinholesStartX = 82;
    /** Maximum x coordinate where a hardware
     * component may be placed on the left
     * pinhole layout. */
    private static int leftPinholesEndX = 214;
    /** Maximum x coordinate where a hardware component may be placed on the left pinhole
     * layout and rotated from vertical to horizontal orientation. */
    public static int leftPinholesXCutoff = leftPinholesStartX + (widthOfPinholeXBucket * 3);

    // Right positioning hardware constraints
    /** Minimum x coordinate where a hardware
     * component may be placed on the right
     * pinhole layout. */
    public static int rightPinholesStartX = 298;
    /** Maximum x coordinate where a hardware
     * component may be placed on the right
     * pinhole layout. */
    private static int rightPinholesEndX = 430;
    /** Maximum x coordinate where a hardware component may be placed on the right pinhole
     * layout and rotated from vertical to horizontal orientation. */
    public static int rightPinholesXCutoff = rightPinholesStartX + (widthOfPinholeXBucket * 3);

    /** Holds the pinhole indices of the positive and negative rail layouts*/
    public static int[][] posNegPinholeIndices = new int[2][29];
    /** Holds the pinhole indices of the (4 x 20) layouts*/
    public static int[][] pinholeIndicesOneToEighty = new int[4][20];
    /** Holds the pinhole indices of the (5 x 12) layouts*/
    public static int[][] pinholeIndicesEightyOneToOneForty = new int[5][12];

    public BreadboardUtil()
    {

    }

    /**
     * Sets the display metrics.
     *
     * @param metrics metrics from getResources().getDisplayMetrics()
     */
    public static void setDisplayMetrics(DisplayMetrics metrics)
    {
        displayMetrics = metrics;
    }

    /**
     * Initializes pinhole indices of
     * all breadboard pinhole layouts.
     */
    public static void initPinholes()
    {
        initializePosNegPinholeIndices(posNegPinholeIndices);
        initializePinholeIndices(pinholeIndicesOneToEighty);
        initializePinholeIndices(pinholeIndicesEightyOneToOneForty);
    }

    /**
     * Converts all default density pixel
     * values to screen pixel values.
     */
    public static void convertAllDpToSp()
    {
        widthOfPinholeXBucket = convertDensityPixelsToScreenPixels(widthOfPinholeXBucket);
        heightOfPinholeYBucket = convertDensityPixelsToScreenPixels(heightOfPinholeYBucket);
        widthAndHeightOfPinhole = convertDensityPixelsToScreenPixels(widthAndHeightOfPinhole);
        yMinimumHardware = convertDensityPixelsToScreenPixels(yMinimumHardware);
        yCutoffHardwareRotated = convertDensityPixelsToScreenPixels(yCutoffHardwareRotated);
        yPadding4x20PinholeLayout = convertDensityPixelsToScreenPixels(yPadding4x20PinholeLayout);
        leftPinholesStartX = convertDensityPixelsToScreenPixels(leftPinholesStartX);
        leftPinholesEndX = convertDensityPixelsToScreenPixels(leftPinholesEndX);
        leftPinholesXCutoff = convertDensityPixelsToScreenPixels(leftPinholesXCutoff);
        rightPinholesStartX = convertDensityPixelsToScreenPixels(rightPinholesStartX);
        rightPinholesEndX = convertDensityPixelsToScreenPixels(rightPinholesEndX);
        rightPinholesXCutoff = convertDensityPixelsToScreenPixels(rightPinholesXCutoff);
    }

    /**
     * Converts a density pixels to screen pixel value.
     *
     * @param pixValue int density pixel value
     *
     * @return screen pixel value
     */
    public static int convertDensityPixelsToScreenPixels(int pixValue)
    {
        return pixValue * (displayMetrics.densityDpi / 160);
    }

    /**
     * Computes and returns the index of a pinhole within a parent layout.
     *
     * @param xLocal x coordinate relative to parent
     * @param yLocal y coordinate relative to parent
     * @param parent 2D int array of indices in the parent layout
     *
     * @return the index of the touched pinhole
     */
    public static int computePinholeIndex(int xLocal, int yLocal, int[][] parent)
    {
        int xIndexWithinParent = xLocal / widthOfPinholeXBucket;
        int yIndexWithinParent = yLocal / heightOfPinholeYBucket;

        return parent[xIndexWithinParent][yIndexWithinParent];
    }

    /**
     * Computes the absolute center coordinates of a touched pinhole from the local pinhole coordinates
     * relative to the parent and from the translation coordinates of the parent.
     *
     * @param xLocal x coordinate relative to parent
     * @param yLocal y coordinate relative to parent
     * @param xParent x translated coordinate of parent top left corner
     * @param yParent y translated coordinate of parent top left corner
     *
     * @return absolute center coordinates of touched pinhole
     */
    public static int[] computePinholeCenterCoordinates(int xLocal, int yLocal, int xParent, int yParent)
    {
        int[] centerCoordinates = new int[2];

        int xTopLeftWithinParent = widthOfPinholeXBucket * (xLocal / widthOfPinholeXBucket);
        int yTopLeftWithinParent = heightOfPinholeYBucket * (yLocal / heightOfPinholeYBucket);

        int xPinholeCenter = xTopLeftWithinParent + xParent + (widthAndHeightOfPinhole / 2);
        int yPinholeCenter = yTopLeftWithinParent + yParent + (widthAndHeightOfPinhole / 2);

        centerCoordinates[0] = xPinholeCenter;
        centerCoordinates[1] = yPinholeCenter;

        return centerCoordinates;
    }

    /**
     * Computes the new coordinates of an hardware component which is directly plugged into the breadboard (resistor, led), based on
     * the nearest pinhole touched y the user.
     *
     * If the user touches a pinhole within the lower (5 x 12) pinhole layout, the new coordinates are returned,
     * otherwise the current coordinates are returned. This prevents the user from placing an on-board hardware
     * component off of the board or directly connecting it to a pin.
     *
     * @param isRotated boolean indicating whether the hardware component has been rotated
     * @param eventX x coordinate of the user's touch
     * @param eventY y coordinate of the users touch
     * @param xCoord previous x coordinate of hardware component
     * @param yCoord previous y coordinate of hardware component
     *
     * @return the computed coordinates of the top left corner of the hardware component
     */
    public static int[] computeComponentCoordinatesOfChangedLocation(Boolean isRotated, int eventX, int eventY, int xCoord, int yCoord)
    {
        int[] coordinates = new int[2];

        int yBucketNumber = ((eventY - yPadding4x20PinholeLayout) / heightOfPinholeYBucket);

        if (isRotated)
        {
            if (eventY > yMinimumHardware && eventY < yCutoffHardwareRotated)
            {
                if (eventX >= leftPinholesStartX && eventX <= leftPinholesEndX)
                {
                    int xBucketNumber = ((eventX - leftPinholesStartX) / widthOfPinholeXBucket);

                    xCoord = xBucketNumber * widthOfPinholeXBucket + leftPinholesStartX;
                    yCoord = yBucketNumber * heightOfPinholeYBucket + yPadding4x20PinholeLayout;
                }

                if (eventX >= rightPinholesStartX && eventX <= rightPinholesEndX)
                {
                    int xBucketNumber = ((eventX - rightPinholesStartX) / widthOfPinholeXBucket);

                    xCoord = xBucketNumber * widthOfPinholeXBucket + rightPinholesStartX;
                    yCoord = yBucketNumber * heightOfPinholeYBucket + yPadding4x20PinholeLayout;
                }
            }
        }
        else
        {
            if (eventY > yMinimumHardware)
            {
                if (eventX >= leftPinholesStartX && eventX <= leftPinholesXCutoff)
                {
                    int xBucketNumber = ((eventX - leftPinholesStartX) / widthOfPinholeXBucket);

                    xCoord = xBucketNumber * widthOfPinholeXBucket + leftPinholesStartX;
                    yCoord = yBucketNumber * heightOfPinholeYBucket + yPadding4x20PinholeLayout;
                }

                if (eventX >= rightPinholesStartX && eventX <= rightPinholesXCutoff)
                {
                    int xBucketNumber = ((eventX - rightPinholesStartX) / widthOfPinholeXBucket);

                    xCoord = xBucketNumber * widthOfPinholeXBucket + rightPinholesStartX;
                    yCoord = yBucketNumber * heightOfPinholeYBucket + yPadding4x20PinholeLayout;
                }
            }
        }

        coordinates[0] = xCoord;
        coordinates[1] = yCoord;

        return coordinates;

    }

    /**
     * Initializes the pinhole indices of the breadboard pinhole
     * layouts in the center of the breadboard.
     *
     * (The 4x20 and 5x12 breadboard pinhole layouts.)
     *
     * @param pinholes 2D int array of pinhole indices
     */
    private static void initializePinholeIndices(int[][] pinholes)
    {
        int index = 0;

        for(int col = 0; col < pinholes[0].length; col++)
        {
            for(int row = 0; row < pinholes.length; row++)
            {
                pinholes[row][col] = index;
                index++;
            }
        }

    }

    /**
     * Initializes the pinhole indices of the breadboard pinhole layouts
     * which hold the positive and negative rails.
     *
     * Note: There is 1 gap (no pinhole) every 5 pinholes.
     *       Gaps are represented by a -1 so that a wire
     *       cannot be drawn to an index of -1.
     *
     * @param pinholes 2D int array of pinhole indices
     */
    private static void initializePosNegPinholeIndices(int[][] pinholes)
    {
        int index = 0;
        int negativeOne = -1;

        for(int col = 0; col < pinholes[0].length; col++)
        {
            for(int row = 0; row < pinholes.length; row++)
            {
                if(col == 5 || col == 11 || col == 17 || col == 23)
                {
                    pinholes[row][col] = negativeOne;
                }
                else
                {
                    pinholes[row][col] = index;
                    index++;
                }
            }
        }

    }

}


