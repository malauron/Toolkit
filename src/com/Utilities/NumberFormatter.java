package com.Utilities;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.function.UnaryOperator;

public class NumberFormatter extends TextFormatter<Double> {

  private static final double DEFAULT_VALUE = 0.00;

  public NumberFormatter() {
    super (
            new StringConverter<Double>() {
              DecimalFormat df = new DecimalFormat("#,##0.00");

              @Override
              public String toString(Double d) {
                return df.format(d);
              }
              @Override
              public Double fromString(String s) {
                try {
                  return df.parse(s).doubleValue();
                } catch (ParseException e) {
                  return Double.parseDouble("0");
                }
              }
            },
            DEFAULT_VALUE,
            new UnaryOperator<TextFormatter.Change>() {
              @Override
              public TextFormatter.Change apply(TextFormatter.Change change) {

                String str;
                String oldStr;

                if (!change.getText().matches("[\\D]")) {

                  if (change.getText().length() > 1 ) {
                    str = change.getText();
                    str = str.replaceAll(",","");

                    if (!str.matches("-?\\d*([\\.]\\d*)?")) {
                      return null;
                    }

                  }
                  return change;

                } else if(change.getText().matches("[\\.\\+\\-]")) {
                  str = change.getText();
                  oldStr = change.getControlText();

                  if (str.equals("-") || str.equals("+")) {

                    if (oldStr.startsWith("-")) {
                      change.setText("");
                      change.setRange(0, 1);
                      change.setCaretPosition(change.getCaretPosition() - 2);
                      change.setAnchor(change.getAnchor() - 2);
                      return change;
                    } else if (!oldStr.startsWith("-") && str.equals("+")) {
                      change.setText("");
                      change.setRange(0, 0);
                      change.setCaretPosition(change.getCaretPosition() - 1);
                      change.setAnchor(change.getAnchor() - 1);
                      return change;
                    } else {
                      change.setRange(0, 0);
                      return change;
                    }
                  } else {
                    long dotCtr = change.getControlNewText().chars().filter(ch -> ch == '.').count();
                    if (dotCtr <= 1) {
                      return change;
                    }
                  }
                }
                return null;
              }
            }
            );
  }

//  UnaryOperator<TextFormatter.Change> textFilter = new UnaryOperator<TextFormatter.Change>() {
//    @Override
//    public TextFormatter.Change apply(TextFormatter.Change change) {
//
//      String str;
//      String oldStr;
//
//      if (!change.getText().matches("[\\D]")) {
//
//        if (change.getText().length() > 1 ) {
//          str = change.getText();
//          str = str.replaceAll(",","");
//
//          if (!str.matches("-?\\d*([\\.]\\d*)?")) {
//            return null;
//          }
//
//        }
//        return change;
//
//      } else if(change.getText().matches("[\\.\\+\\-]")) {
//        str = change.getText();
//        oldStr = change.getControlText();
//
//        if (str.equals("-") || str.equals("+")) {
//
//          if (oldStr.startsWith("-")) {
//            change.setText("");
//            change.setRange(0, 1);
//            change.setCaretPosition(change.getCaretPosition() - 2);
//            change.setAnchor(change.getAnchor() - 2);
//            return change;
//          } else if (!oldStr.startsWith("-") && str.equals("+")) {
//            change.setText("");
//            change.setRange(0, 0);
//            change.setCaretPosition(change.getCaretPosition() - 1);
//            change.setAnchor(change.getAnchor() - 1);
//            return change;
//          } else {
//            change.setRange(0, 0);
//            return change;
//          }
//        } else {
//          long dotCtr = change.getControlNewText().chars().filter(ch -> ch == '.').count();
//          if (dotCtr <= 1) {
//            return change;
//          }
//        }
//      }
//      return null;
//    }
//  };

//  StringConverter<Double> numberConverter = new StringConverter<Double>() {
//    DecimalFormat df = new DecimalFormat("#,##0.00");
//
//    @Override
//    public String toString(Double d) {
//      System.out.println("ConvToString");
//      return df.format(d);
//    }
//    @Override
//    public Double fromString(String s) {
//      System.out.println("ConvFromString");
//      try {
//        return df.parse(s).doubleValue();
//      } catch (ParseException e) {
//        return Double.parseDouble("0");
//      }
//    }
//  };
}
