package com.zzhl.util;

/**
 * <p>Created: 2017-08-25</p>
 *
 * @author andy
 **/
public class NumberUtils {

    public static boolean isValidId(Integer id) {
        return id != null && id.intValue() > 0;
    }

    public static boolean isValidNumberId(Long numberId) {
        return numberId != null && numberId > 0;
    }

}
