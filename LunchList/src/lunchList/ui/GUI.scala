package lunchList.ui

import scala.swing._
import scala.swing.event._
import java.awt.Font._
import lunchList._
import javax.swing.{ToolTipManager, UIManager}
import javax.swing.ImageIcon
import java.net.URL
import java.awt.Color
import java.awt.{event => jae}
import scala.collection.mutable.ArrayBuffer

object GUI extends SimpleSwingApplication {
  
  ToolTipManager.sharedInstance.setInitialDelay(150)
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

  def top = new MainFrame {
    title    = "Lunch List"
    minimumSize   = new Dimension(720, 480)
    preferredSize = new Dimension(1600, 900)
    maximumSize   = new Dimension(1920, 1080)
    location      = new Point(50, 50)
    this.pack()

    
    
    var day = Tools.Today
    val user = new User("Phi")
    
    
    
    // Box panels:
    
    val ABlocBox = new BoxPanel(Orientation.Vertical)  {
      preferredSize = new Dimension(600, 50)
      background = Color.WHITE
    }
    val DipoliBox = new BoxPanel(Orientation.Vertical) {
      preferredSize = new Dimension(600, 50)
      background = Color.WHITE
    }
    val KvarkkiBox = new BoxPanel(Orientation.Vertical) {
      preferredSize = new Dimension(1000, 50)
      background = Color.WHITE
    }
    val TaffaBox = new BoxPanel(Orientation.Vertical) {
      preferredSize = new Dimension(600, 50)
      background = Color.WHITE
    }
    val MaukasBox = new BoxPanel(Orientation.Vertical) {
      preferredSize = new Dimension(600, 50)
      background = Color.WHITE
    }
    val boxList = Vector(ABlocBox, DipoliBox, KvarkkiBox, TaffaBox, MaukasBox)
    
    
    
    
    // Scroll panes:
    
    val ABlocScrollPane = new ScrollPane(ABlocBox) {
      preferredSize = new Dimension(300, 50)
    }
    val KvarkkiScrollPane = new ScrollPane(KvarkkiBox) {
      preferredSize = new Dimension(300, 50)
    }
    val DipoliScrollPane = new ScrollPane(DipoliBox) {
      preferredSize = new Dimension(300, 50)
    }
    val TaffaScrollPane = new ScrollPane(TaffaBox) {
      preferredSize = new Dimension(300, 50)
    }
    val MaukasScrollPane = new ScrollPane(MaukasBox) {
      preferredSize = new Dimension(300, 50)
    }
    
    
    
    
    // Properties check boxes:
    
    val G_checkBox = new CheckBox("G") { tooltip = "Gluten-free" }
    val L_checkBox = new CheckBox("L") { tooltip = "Lactose-free" }
    val VL_checkBox = new CheckBox("VL") { tooltip = "Low lactose" }
    val M_checkBox = new CheckBox("M") { tooltip = "Milk-free" }
    val Star_checkBox = new CheckBox("*") { tooltip = "Well-being" }
    val VS_checkBox = new CheckBox("VS") { tooltip = "Contains fresh garlic" }
    val A_checkBox = new CheckBox("A+") { tooltip = "Allergens" }
    val E_checkBox = new CheckBox("E") { tooltip = "Egg-free" }
    val H_checkBox = new CheckBox("H") { tooltip = "Healthier options" }  
    val S_checkBox = new CheckBox("S") { tooltip = "Soy-free" }
    val foodTypeList = Vector(G_checkBox, L_checkBox, VL_checkBox, M_checkBox, Star_checkBox, VS_checkBox, A_checkBox, E_checkBox, H_checkBox, S_checkBox)
    foodTypeList.foreach({ checkBox =>
      this.listenTo(checkBox)
      this.reactions += {
        case ButtonClicked(`checkBox`) =>
          val clickedButton = checkBox
          val textOnButton = checkBox.text
          if (clickedButton.selected)
            user.favouriteType += textOnButton
          else
            user.favouriteType -= textOnButton
          this.updateInfo()
      }
    })
    
    
    
    
    // Properties:
    
    val properties = new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Properties") {
        font = new Font("Ubuntu", PLAIN, 15)
      }
      contents += Swing.HStrut(20)
      contents ++= foodTypeList
    }
    
    
    
    // Search:
    
    val allergensTextField = new TextField(40) {
      this.tooltip = "E.g. milk, beef, fish. \nPress `Enter` after finish."
    }
    
    val predicatesTextField = new TextField(40) {
      this.tooltip = "E.g. milk, beef, fish. Press `Enter` after finish."
    }
    
    val predicates = new BoxPanel(Orientation.Horizontal) {
      contents += new Label("   🔍") {
        font = new Font("Ubuntu", PLAIN, 20)
      }
      contents += Swing.HStrut(52)
      contents += predicatesTextField
    }
    
    val allergens = new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Allergens") {
        font = new Font("Ubuntu", PLAIN, 15)
      }
      contents += Swing.HStrut(30)
      contents += allergensTextField
    }
    
    val searchBox = new BoxPanel(Orientation.Vertical) {
      contents += predicates
      contents += Swing.VStrut(5)
      contents += allergens
    }
    
    
    
    // Favourite button:
    
    var favouriteON = false
    val favouriteButton = new ToggleButton("Favourite: OFF") {
      if (!this.selected)
        this.tooltip = "Enable to display only favourite items (Ctrl + F)."
      else
        this.tooltip = "Disable to display all dishes (Ctrl + F)."
      preferredSize = new Dimension(200, 40)
      font = new Font("Serif", BOLD, 20)
    }
    
    
    
    // Day drop-down menu:
    
    val dates_stringToDays = Tools.daysFromNowToString(7).zip(Tools.daysFromNow_getDayOfWeek(7)).toMap
    val dayComboBox = new ComboBox(Tools.daysFromNowToString(7)) {
      preferredSize = new Dimension(200, 40)
      font = new Font("Serif", PLAIN, 20)
      tooltip = "Day in a week"
    }
    
    
    
    updateInfo()
    
    
    
    // Listen:
    
    this.listenTo(dayComboBox.selection)
    this.listenTo(dayComboBox.keys)
    foodTypeList.foreach(this.listenTo(_))
    this.listenTo(allergensTextField)
    this.listenTo(allergensTextField.keys)
    this.listenTo(predicatesTextField)
    this.listenTo(predicatesTextField.keys)
    boxList.foreach({ box =>
      this.listenTo(box)
      this.listenTo(box.keys)
    })
    this.listenTo(favouriteButton)
    this.listenTo(favouriteButton.keys)
    
    
    
    
    // Events:
    
    var pressedKey = ArrayBuffer[Key.Value]()
    this.reactions += {
      case SelectionChanged(`dayComboBox`) => 
        day = dates_stringToDays(dayComboBox.selection.item)
        this.updateInfo()
        
      case ButtonClicked(`favouriteButton`) =>
        if (favouriteButton.selected) {
          favouriteButton.text = "Favourite: ON"
          favouriteButton.tooltip = "Disable to display all dishes (Ctrl + F)."
          favouriteON = true
        }
        else {
          favouriteButton.text = "Favourite: OFF"
          favouriteButton.tooltip = "Enable to display only favourite items (Ctrl + F)."
          favouriteON = false
        }
        updateInfo()
        
      case c: KeyPressed =>
        if (c.key != Key.Alt && c.key != Key.F4 && (pressedKey.isEmpty || c.key != pressedKey.last))
          pressedKey += c.key
        if (pressedKey.length >= 2 && pressedKey(0) == Key.Control && pressedKey(1) == Key.F)
          favouriteONOFF()
        if ((c.source == this.allergensTextField || c.source == this.predicatesTextField) && c.key == Key.Enter) {
          val text1 = allergensTextField.text.trim
          if (text1.nonEmpty)
            user.allergens = text1.split(',').map(_.trim())
          else
            user.allergens = Array[String]()
            
          val text2 = predicatesTextField.text.trim
          if (text2.nonEmpty) 
            user.predicates = text2.split(',').map(_.trim())
          else
            user.predicates = Array[String]()
          this.updateInfo()
        }
          
      case c: KeyReleased =>
        pressedKey -= c.key
        
    }
    
    
      
    // Layout
    // A box panel inside a scroll pane panel contains everything:
    
    val top2 = new BoxPanel(Orientation.Vertical) {
      this.contents += new GridBagPanel { 
        import scala.swing.GridBagPanel.Anchor._
        import scala.swing.GridBagPanel.Fill
        layout += dayComboBox                -> new Constraints(0, 0, 3, 1, 0, 0, East.id,      Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += favouriteButton            -> new Constraints(0, 0, 3, 1, 0, 0, Center.id,    Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += searchBox                  -> new Constraints(0, 0, 3, 1, 0, 0, NorthWest.id, Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += properties                 -> new Constraints(0, 1, 3, 1, 0, 0.05, NorthWest.id, Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += new Label(ABloc.name)    { font = new Font("Serif", ITALIC , 20); this.foreground = Color.BLUE }    -> new Constraints(0, 2, 1, 1, 0, 0, Center.id, Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += new Label(Kvarkki.name)  { font = new Font("Serif", ITALIC , 20); this.foreground = Color.BLUE }    -> new Constraints(1, 2, 1, 1, 0, 0, Center.id, Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += new Label(Maukas.name)   { font = new Font("Serif", ITALIC , 20); this.foreground = Color.BLUE }    -> new Constraints(2, 2, 1, 1, 0, 0, Center.id, Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += new Label(Taffa.name)    { font = new Font("Serif", ITALIC , 20); this.foreground = Color.BLUE }    -> new Constraints(0, 4, 1, 1, 0, 0, Center.id, Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += new Label(Dipoli.name)    { font = new Font("Serif", ITALIC , 20); this.foreground = Color.BLUE }   -> new Constraints(1, 4, 1, 1, 0, 0, Center.id, Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += ABlocScrollPane            -> new Constraints(0, 3, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += KvarkkiScrollPane          -> new Constraints(1, 3, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += MaukasScrollPane           -> new Constraints(2, 3, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += TaffaScrollPane            -> new Constraints(0, 5, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, new Insets(5, 5, 5, 5), 0, 0)
        layout += DipoliScrollPane           -> new Constraints(1, 5, 1, 1, 1, 1, NorthWest.id, Fill.Both.id, new Insets(5, 5, 5, 5), 0, 0)
        this.peer.addComponentListener(new jae.ComponentAdapter {
          override def componentResized(e: jae.ComponentEvent): Unit = {
            if (size.getWidth < 1210)
              layout -= favouriteButton
            else
              layout += favouriteButton      -> new Constraints(0, 0, 3, 1, 0, 0, Center.id,    Fill.None.id, new Insets(5, 5, 5, 5), 0, 0)
          }
        })      
      }
      this.preferredSize = new Dimension(720, 800)
    }
    
    this.contents = new ScrollPane(top2)
    
    
    
    // Menu:
    
    this.menuBar = new MenuBar {
      contents += new Menu("Program") {
        val quitAction        = Action("Quit                                              Alt+F4") { closeOperation() }
        val adjustFavAction   = Action("Favourite ON/OFF                        Ctrl+F")           { favouriteONOFF() }
        
        contents += new MenuItem(quitAction)
      }
      contents += new Menu("Edit") {
        val resetFavAction    = Action("Reset Favourites                         Ctrl+R")          { resetFavourite; updateInfo }
        contents += new MenuItem(resetFavAction)
      }
    }
    
    
    
    def updateInfo() = {
      updateBox(ABlocBox, ABloc)
      updateBox(DipoliBox, Dipoli)
      updateBox(KvarkkiBox, Kvarkki)
      updateBox(TaffaBox, Taffa)
      updateBox(MaukasBox, Maukas)
    }
    
    
    
    /** Update data inside a box panel of a `Restaurant` and repaint the box*/
    def updateBox(box: BoxPanel, res: Restaurant) = {
      val menuText = {
        if (favouriteON)
          res.textMenuFilteredByFavouriteCourses(day, user)
        else
          res.textMenu(day, user.favouriteType, user.allergens, user.predicates)
      }
      
      var courses = ArrayBuffer[Component]()
      val lineText = menuText.split('\n').filterNot(_.isEmpty())
      if (!Restaurant.notification.contains(menuText)) {
        for (line <- lineText) {
          if (!Restaurant.notification.contains(line.trim)) {
            
            val courseTick = new CheckBox(line.trim()) {
                font = new Font("Monaco", BOLD , 15)
                background = Color.white
                iconTextGap = 10
                if (user.favouriteCourse.contains(res) && user.favouriteCourse(res).contains(line.trim())) {
                  selected = true
                  tooltip = "Click to remove from favourites."
                }
                else
                  tooltip = "Click to add to favourites."
            }
                
            courses += courseTick
            this.listenTo(courseTick)
                
            this.reactions += {
              case ButtonClicked(`courseTick`) =>
                if (courseTick.selected) {
                  if (user.favouriteCourse.contains(res))
                    user.favouriteCourse(res) += courseTick.text
                  else
                    user.favouriteCourse(res) = ArrayBuffer(courseTick.text)
                  courseTick.tooltip = "Click to remove from favourites."
                }
                else {
                  user.favouriteCourse(res) -= courseTick.text
                  courseTick.tooltip = "Click to add to favourites."
                }
            }
          }
          
          else {
            val noMenuLabel = new Label(line) {
              font = new Font("Monaco", BOLD , 15)
            }
            courses += noMenuLabel
          }
              
        }
      }
      else {
        for (line <- lineText) {
          val noMenuLabel = new Label(line) {
            font = new Font("Monaco", BOLD , 15)
          }
          courses += noMenuLabel
        }
      }
      box.contents.clear()
      box.contents ++= courses
      box.repaint()
      box.revalidate()
    }
    
    
    
    def favouriteONOFF() = {
      if (favouriteButton.selected) {
        favouriteButton.selected = false
        favouriteButton.text = "Favourite: OFF"
        favouriteButton.tooltip = "Enable to display only favourite items (Ctrl + F)."
        favouriteON = false
      }
      else {
        favouriteButton.selected = true
        favouriteButton.text = "Favourite: ON"
        favouriteButton.tooltip = "Disable to display all dishes (Ctrl + F)."
        favouriteON = true
      }
      updateInfo()
    }
    
    
    
    def resetFavourite {
      user.favouriteCourse.clear()
    }
    
    
    
    override def closeOperation() {
      visible = true
      val res = Dialog.showConfirmation(null, "Do you want to save your favourite data?", optionType = Dialog.Options.YesNoCancel, title = title)
      if (res == Dialog.Result.Ok)
        FileProcessing.saveFileFavourite(user)
      else if (res == Dialog.Result.No)
        FileProcessing.clearFileFavourite(user)
      if (res != Dialog.Result.Closed && res != Dialog.Result.Cancel)
        dispose()
    }
    
    
  }
}