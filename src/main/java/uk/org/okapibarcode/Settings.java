/*
 * Copyright 2015 Robin Stuart
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.org.okapibarcode;

import com.beust.jcommander.Parameter;
import uk.org.okapibarcode.backend.HumanReadableLocation;
import uk.org.okapibarcode.graphics.Color;

/**
 * @author <a href="mailto:rstuart114@gmail.com">Robin Stuart</a>
 */
public class Settings {

    @Parameter(names = "-cli", description = "Supress GUI loading", required = false)
    private boolean supressGui = false;

    @Parameter(names = { "-t", "--types" }, description = "Display table of barcode types", required = false)
    private boolean displayTypes = false;

    @Parameter(names = { "-i", "--input" }, description = "Read data from file", required = false)
    private String inputFile = "";

    @Parameter(names = { "-o", "--output" }, description = "Write image to file", required = false)
    private String outputFile = "out.png";

    @Parameter(names = { "-d", "--data" }, description = "Barcode content", required = false)
    private String inputData = "";

    @Parameter(names = { "-b", "--barcode" }, description = "Select barcode type", required = false)
    private int symbolType = 20;

    @Parameter(names = "--height", description = "Height of the symbol in multiples of x-dimension", required = false)
    private int symbolHeight = 0;

    //    @Parameter(names = {"-w", "--whitesp"}, description = "Width of whitespace in multiples of x-dimension", required = false)
    //    private int symbolWhiteSpace = 0;
    //
    //    @Parameter(names = "--border", description = "Width of border in multiples of x-dimension", required = false)
    //    private int symbolBorder = 0;
    //    @Parameter(names = "--box", description = "Add a box", required = false)
    //    private boolean addBox = false;
    //
    //    @Parameter(names = "--bind", description = "Add boundary bars", required = false)
    //    private boolean addBinding = false;
    @Parameter(names = { "-r", "--reverse" }, description = "Reverse colours (white on black)", required = false)
    private boolean reverseColour = false;

    @Parameter(names = "--fg", description = "Specify a foreground (ink) colour", required = false)
    private String foregroundColour = "000000";

    @Parameter(names = "--bg", description = "Specify a background (paper) colour", required = false)
    private String backgroundColour = "FFFFFF";

    @Parameter(names = "--scale", description = "Adjust size of output image", required = false)
    private int symbolScale = 0;

    // --directpng, --directeps, --directsvg, --dump
    //    @Parameter(names = "--rotate", description = "Rotate symbol", required = false)
    //    private int rotationAngle = 0;
    @Parameter(names = "--cols", description = "Number of columns in PDF417", required = false)
    private int symbolColumns = 0;

    @Parameter(names = "--vers", description = "Set QR Code version number", required = false)
    private int symbolVersion = 0;

    @Parameter(names = "--secure", description = "Set error correction level", required = false)
    private int symbolECC = 0;

    @Parameter(names = "--primary", description = "Add structured primary message", required = false)
    private String primaryData = "";

    @Parameter(names = "--mode", description = "Set encoding mode", required = false)
    private int encodeMode = 0;

    @Parameter(names = "--gs1", description = "Treat input as GS1 data", required = false)
    private boolean dataGs1Mode = false;

    @Parameter(names = "--binary", description = "Treat input as binary data", required = false)
    private boolean dataBinaryMode = false;

    @Parameter(names = "--notext", description = "Remove human readable text", required = false)
    private boolean supressHrt = false;

    @Parameter(names = "--textabove", description = "Place human readable text above symbol", required = false)
    private boolean superHrt = false;

    @Parameter(names = "--square", description = "Force Data Matrix symbols to be square", required = false)
    private boolean makeSquare = false;

    @Parameter(names = "--init", description = "Add reader initialisation code", required = false)
    private boolean addReaderInit = false;

    // --smalltext
    @Parameter(names = "--batch", description = "Treat each line of input as a separate data set", required = false)
    private boolean batchMode = false;

    /**
     * @return the supressGui
     */
    public boolean isGuiSupressed() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the displayTypes
     */
    public boolean isDisplayTypes() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the inputFile
     */
    public String getInputFile() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the outputFile
     */
    public String getOutputFile() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the inputData
     */
    public String getInputData() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the symbolType
     */
    public int getSymbolType() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the symbolHeight
     */
    public int getSymbolHeight() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //    /**
    //     * @return the symbolWhiteSpace
    //     */
    //    public int getSymbolWhiteSpace() {
    //        return symbolWhiteSpace;
    //    }
    //    /**
    //     * @return the symbolBorder
    //     */
    //    public int getSymbolBorder() {
    //        return symbolBorder;
    //    }
    //    /**
    //     * @return the addBox
    //     */
    //    public boolean isAddBox() {
    //        return addBox;
    //    }
    //
    //    /**
    //     * @return the addBinding
    //     */
    //    public boolean isAddBinding() {
    //        return addBinding;
    //    }
    /**
     * @return the reverseColour
     */
    public boolean isReverseColour() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the foregroundColour
     */
    public Color getForegroundColour() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the backgroundColour
     */
    public Color getBackgroundColour() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the symbolScale
     */
    public int getSymbolScale() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    //    /**
    //     * @return the rotationAngle
    //     */
    //    public int getRotationAngle() {
    //        return rotationAngle;
    //    }
    /**
     * @return the symbolColumns
     */
    public int getSymbolColumns() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the symbolVersion
     */
    public int getSymbolVersion() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the symbolECC
     */
    public int getSymbolECC() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the primaryData
     */
    public String getPrimaryData() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the encodeMode
     */
    public int getEncodeMode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the dataGs1Mode
     */
    public boolean isDataGs1Mode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the dataBinaryMode
     */
    public boolean isDataBinaryMode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the supressHrt
     */
    public HumanReadableLocation getHrtPosition() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the makeSquare
     */
    public boolean isMakeSquare() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the addReaderInit
     */
    public boolean isReaderInit() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }

    /**
     * @return the batchMode
     */
    public boolean isBatchMode() {
        throw new UnsupportedOperationException("STUB: not implemented");
    }
}
