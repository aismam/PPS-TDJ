package View.EventHandlers

import Cache.TowerDefenseCache
import Configuration.DefaultConfig.{CACHE_GENERIC_ERROR, NOTHING_MESSAGE, STAGE_ERROR}
import Controller.{DrawingManager, GameController, UpdateManager}
import Model.Tower.TowerTypes
import Model.Tower.TowerTypes.{BASE_TOWER, CANNON_TOWER, FLAME_TOWER}
import Utility.Utils
import View.ViewController.{GameViewController, MainMenuViewController}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input.MouseEvent
import scalafx.application.JFXApp3.PrimaryStage

/**
 * This class contains all event handler that is used from the Game Controller
 * @param gameViewController used to graphic updates
 * @param gameController used to change the status of the game
 */
sealed class GameEventHandlers private(gameViewController: GameViewController, gameController: GameController)
  extends EventHandlers {

  def onCellClickedEventHandler(): EventHandler[MouseEvent] = {
    (event: MouseEvent) => {
      if (gameController.gameStarted) {
        gameController.onCellClicked(event.getX, event.getY)
        //Draw tower on tile
        val tower = gameController.selected_tower
        tower match {
          case None =>
          case Some(tower) =>
            DrawingManager.drawTower(tower.posX, tower.posY, tower.graphic(), gameViewController)
        }
      }
    }
  }

  def startWave(): EventHandler[ActionEvent] = {
    (_: ActionEvent) => {
      gameController.onPlayButton()
    }
  }

  def goMainMenu(primaryStage: Option[PrimaryStage]): EventHandler[ActionEvent] = {
    (_: ActionEvent) => {
      primaryStage match {
        case None => logger.error(STAGE_ERROR)
        case Some(primaryStage: PrimaryStage) =>
          val mainMenuViewController: MainMenuViewController = MainMenuViewController.apply(primaryStage)
          primaryStage.setScene(mainMenuViewController.menuViewModel.scene)
      }
    }
  }

  def selectTower(towerTypes: TowerTypes.TowerType): EventHandler[ActionEvent] = {
    (_: ActionEvent) => {
      towerTypes match {
        case BASE_TOWER =>
          val tower = gameController.available_towers.get(BASE_TOWER)
          gameController.buildTower(tower.get)
          logger.info("Select tower {} ", tower.get.name())
        case CANNON_TOWER =>
          val tower = gameController.available_towers.get(CANNON_TOWER)
          gameController.buildTower(tower.get)
          logger.info("Select tower {} ", tower.get.name())
        case FLAME_TOWER =>
          val tower = gameController.available_towers.get(FLAME_TOWER)
          gameController.buildTower(tower.get)
          logger.info("Select tower {} ", tower.get.name())
        case _ =>
          logger.warn("Non implemented yet")
      }
    }
  }

  def restartGame(primaryStage: Option[PrimaryStage]): EventHandler[ActionEvent] = {
    (_: ActionEvent) => {

      val playerName = TowerDefenseCache.playerName
      val difficulty = TowerDefenseCache.difficulty

      var gameController: GameController = null

      TowerDefenseCache.gameType match {
        case Some(value) =>
          difficulty match {
            case Some(dif) =>
              if (value) gameController = GameController(playerName.getOrElse(Utils.getRandomName()), dif)
              else gameController = GameController(playerName.getOrElse(Utils.getRandomName()), dif)
            case None => logger.error(CACHE_GENERIC_ERROR)
          }
        case None => logger.error(CACHE_GENERIC_ERROR)
      }

      primaryStage match {
        case Some(stage) =>
          val gameViewController: GameViewController = GameViewController(stage, gameController)
          setScene(stage,gameViewController,playerName.getOrElse("No Name"),difficulty.getOrElse(1))

          DrawingManager.drawGrid(gameController, gameViewController)
          //start loop
          UpdateManager.apply(gameController, gameViewController).run().start()
        case None => logger.error(STAGE_ERROR)

      }
    }
  }

  def nothing(): EventHandler[ActionEvent] = {
    (_: ActionEvent) => {
      logger.error(NOTHING_MESSAGE)
    }
  }

}

object GameEventHandlers {

  def apply(gameViewController: GameViewController, gameController: GameController): GameEventHandlers =
    new GameEventHandlers(gameViewController, gameController)
}