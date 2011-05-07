package main.scala

object Main {
  def main(args:Array[String]):Unit = {
    new ExcelImage(new Config(args(0))).makeExcel
  }
}

