package me.mdbell.noexs.ui.controllers;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.mdbell.noexs.ui.models.ConversionType;
import me.mdbell.util.HexUtils;

public class ConversionUtils {

    private static final Logger logger = LogManager.getLogger(ConversionUtils.class);

    public static Long sourceToLong(ConversionType convType, String rawValue) {
        Long res = null;

        if (StringUtils.startsWithIgnoreCase(rawValue, "0x")) {
            String value = StringUtils.trim(StringUtils.removeStartIgnoreCase(rawValue, "0x"));
            switch (convType) {
                case FLT:
                    Float f = Float.intBitsToFloat(Integer.parseUnsignedInt(value, 16));
                    res = (long) Float.floatToIntBits(f);
                    break;
                case U8:
                    res = (long) Byte.parseByte(value, 16);
                    break;
                case U16:
                    res = (long) Short.parseShort(value, 16);
                    break;
                case U32:
                    res = (long) Integer.parseUnsignedInt(value, 16);
                    break;
                case U64:
                    res = Long.parseUnsignedLong(value, 16);
                    break;
                case S8:
                    res = (long) Byte.parseByte(value, 16);
                    break;
                case S16:
                    res = (long) Short.parseShort(value, 16);
                    break;
                case S32:
                    res = (long) Integer.parseInt(value, 16);
                    break;
                case S64:
                    res = Long.parseLong(value, 16);
                    break;
                default:
                    logger.error("Conversion type not supported : {}", convType);
                    break;
            }
        } else {
            String value = StringUtils.trim(rawValue);
            switch (convType) {
                case FLT:
                    Float f = Float.parseFloat(value);
                    res = (long) Float.floatToIntBits(f);
                    break;
                case U8:
                    res = (long) Byte.parseByte(value);
                    break;
                case U16:
                    res = (long) Short.parseShort(value);
                    break;
                case U32:
                    res = (long) Integer.parseUnsignedInt(value);
                    break;
                case U64:
                    res = Long.parseUnsignedLong(value);
                    break;
                case S8:
                    res = (long) Byte.parseByte(value);
                    break;
                case S16:
                    res = (long) Short.parseShort(value);
                    break;
                case S32:
                    res = (long) Integer.parseInt(value);
                    break;
                case S64:
                    res = Long.parseLong(value);
                    break;
                default:
                    logger.error("Conversion type not supported : {}", convType);
                    break;
            }
        }

        return res;
    }

    public static String longToString(ConversionType convTypeSource, ConversionType convTypeDest, long value,
            boolean hex, boolean hexPrefix) {
        String res = null;
        if (hex) {
            int length = convTypeDest.getLength() * 2;
            String conv = null;
            switch (convTypeDest) {
                case FLT:
                    Float f = null;
                    if (convTypeSource == ConversionType.FLT) {
                        conv = Long.toHexString(value);
                    } else {
                        f = (float) value;
                        conv = Long.toHexString(Float.floatToIntBits(f));
                    }
                    break;
                case U8:
                case U16:
                case U32:
                case U64:
                case S8:
                case S16:
                case S32:
                case S64:
                case ADDR:
                    conv = Long.toHexString(value);

                    break;
                default:
                    logger.error("Destionation conversion type not supported : {}", convTypeDest);
                    break;
            }

            int end = conv.length();
            int start = Math.max(0, end - length);
            conv = StringUtils.substring(conv, start, end);
            res = HexUtils.pad('0', length, conv);

            res = StringUtils.upperCase(res);
            if (hexPrefix) {
                res = "0x" + res;
            }
        } else {
            switch (convTypeDest) {
                case FLT:
                    Float f = null;
                    if (convTypeSource == ConversionType.FLT) {
                        f = Float.intBitsToFloat((int) value);
                    } else {
                        f = (float) value;
                    }
                    res = Float.toString(f);
                    break;
                case U8:
                    res = Byte.toString((byte) value);
                    break;
                case U16:
                    res = Short.toString((short) value);
                    break;
                case U32:
                    res = Integer.toUnsignedString((int) value);
                    break;
                case ADDR:
                case U64:
                    res = Long.toUnsignedString(value);
                    break;
                case S8:
                    res = Byte.toString((byte) value);
                    break;
                case S16:
                    res = Short.toString((short) value);
                    break;
                case S32:
                    res = Integer.toString((int) value);
                    break;
                case S64:
                    res = Long.toString(value);
                    break;
                default:
                    logger.error("Destionation conversion type not supported : {}", convTypeDest);
                    break;
            }
        }

        return res;
    }

}
