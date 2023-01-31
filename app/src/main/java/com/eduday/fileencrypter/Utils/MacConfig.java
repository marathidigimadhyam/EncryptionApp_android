package com.eduday.fileencrypter.Utils;

/**
 * Represents a MAC configuration: key length, tag length.
 * All lengths are in bytes.
 */
public enum MacConfig {

    DEFAULT((byte) 1, 64, 20);

    public final byte macId;
    public final int keyLength;
    public final int tagLength;

    /**
     * Returns the size of the header added when encoding.
     * It contains 2 bytes for format+macId.
     */
    public int getHeaderLength() {
        return 2;
    }

    /**
     * Returns the size of the tail added when encoding.
     * It's only the authentication tag.
     */
    public int getTailLength() {
        return tagLength;
    }

    MacConfig(byte macId, int keyLength, int tagLength) {
        this.macId = macId;
        this.keyLength = keyLength;
        this.tagLength = tagLength;
    };
}