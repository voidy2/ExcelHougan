package main.scala

import org.apache.poi.util._
import org.apache.poi.hssf.usermodel._
import org.apache.commons.io.FilenameUtils
import java.io._
import java.util.{ArrayList,LinkedHashMap}

class Config(configFilename:String) {
  import org.yaml.snakeyaml.Yaml
  val param = new Yaml().load(new FileReader(configFilename))
      .asInstanceOf[LinkedHashMap[String,Any]]
  lazy val excelFilename = param.get("excel_filename").toString
  lazy val header = param.get("header").toString
  lazy val images = param.get("images")
    .asInstanceOf[ArrayList[String]]
}

trait Excel {
  val workbook = new HSSFWorkbook

  def write(filename:String):Unit = {
    val fileOut = new FileOutputStream(filename)
    workbook.write(fileOut)
    fileOut.close
  }

  def makeExcel

}

/** 副作用しかない・・・ */
class ExcelSheet(_sheet:HSSFSheet) {
  val sheet = _sheet
  val defaultFooter = "%s/%s".format(
    HeaderFooter.page,HeaderFooter.numPages)

  def setPrintSize(fitWidth:Short=1, fitHeight:Short=1):Unit = {
    val ps = sheet.getPrintSetup()
    sheet.setAutobreaks(true)
    ps.setFitHeight(fitWidth)
    ps.setFitWidth(fitHeight)
  }

  def setSheetHeaderFooter(header:String, 
    footer:String=defaultFooter):Unit = {
    sheet.getHeader.setCenter(header)
    sheet.getFooter.setCenter(footer)
  }

}

class ExcelImage (_config:Config) extends Excel {
  val config = _config

  override def makeExcel():Unit = {
    def makeSheet(filename:String):Unit = {
      val sheet = new ExcelSheet(createInsertedImageSheet(filename))
      sheet.setPrintSize()
      sheet.setSheetHeaderFooter(config.header)
    }
    import scala.collection.JavaConversions._
    config.images.foreach(makeSheet)
    write(config.excelFilename)
  }

  def createInsertedImageSheet(filename:String):HSSFSheet = {
    val sheetname = FilenameUtils.getBaseName(filename)
    val sheet = workbook.createSheet(sheetname)
    val bytes = IOUtils.toByteArray(new FileInputStream(filename))
    val pictureIndex = workbook.addPicture(bytes,
      FilenameUtils.getExtension(filename) match {
        case "jpg" | "jpeg" => HSSFWorkbook.PICTURE_TYPE_JPEG
        case "png" => HSSFWorkbook.PICTURE_TYPE_PNG
        case "bmp" => HSSFWorkbook.PICTURE_TYPE_DIB
      }
    )
    val clientAnchor = workbook.getCreationHelper.createClientAnchor()
    //表示位置のインデックスを設定
    clientAnchor.setCol1(0)
    clientAnchor.setRow1(0)
    sheet.createDrawingPatriarch.
      createPicture(clientAnchor,pictureIndex).resize()

    sheet
  }

}

