package com.roomster.roomsterbackend.util.validator;

public class ValidatorUtils {

    public static final String NUMBER_REGEX = "^[0-9]+$";

    public static boolean isNumber(String number){
        if(number != null && !number.isEmpty()){
            return number.matches(NUMBER_REGEX);
        }
        return false;
    }

}
