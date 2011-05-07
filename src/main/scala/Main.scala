package main.scala

object Main {
  def main(args:Array[String]):Unit = {
    new ExcelImage(new Config("config.yml")).makeExcel
  }
}

