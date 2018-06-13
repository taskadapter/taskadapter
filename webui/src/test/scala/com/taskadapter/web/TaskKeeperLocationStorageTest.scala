package com.taskadapter.web

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib.TempFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TaskKeeperLocationStorageTest extends FunSpec with Matchers with TempFolder {
  it("keeps existing data when called with empty results") {
    withTempFolder { folder =>
      val (sourceId, targetId) = createIds(1, 100)
      val storage = new TaskKeeperLocationStorage(folder)
      storage.store("location1", "location2", Seq((sourceId, targetId)))
      storage.store("location1", "location2", Seq())
      val loaded = storage.loadTasks("location1", "location2")
      loaded.findSourceSystemIdentity(sourceId, "location2") shouldBe Some(targetId)
    }
  }

  it("can save and load data") {
    withTempFolder { folder =>
      val (sourceId, targetId) = createIds(1, 100)
      val storage = new TaskKeeperLocationStorage(folder)
      storage.store("location1", "location2", Seq((sourceId, targetId)))

      val loaded = storage.loadTasks("location1", "location2")
      loaded.findSourceSystemIdentity(sourceId, "location2") shouldBe Some(targetId)
    }
  }

  it("adds new results to existing data") {
    withTempFolder { folder =>
      val (sourceId, targetId) = createIds(1, 100)
      val storage = new TaskKeeperLocationStorage(folder)
      storage.store("location1", "location2", Seq((sourceId, targetId)))

      val (anotherSourceId, anotherTargetId) = createIds(2, 200)
      storage.store("location1", "location2", Seq((anotherSourceId, anotherTargetId)))

      val loaded = storage.loadTasks("location1", "location2")
      loaded.findSourceSystemIdentity(sourceId, "location2") shouldBe Some(targetId)
      loaded.findSourceSystemIdentity(anotherSourceId, "location2") shouldBe Some(anotherTargetId)
    }
  }

  it("skips duplicate elements on append") {
    withTempFolder { folder =>
      val (sourceId, targetId) = createIds(1, 100)
      val storage = new TaskKeeperLocationStorage(folder)
      storage.store("location1", "location2", Seq((sourceId, targetId)))
      // add the same element again
      storage.store("location1", "location2", Seq((sourceId, targetId)))

      val loaded = storage.loadTasks("location1", "location2")
      loaded.mapLeftToRight.keySet.size shouldBe 1
    }
  }

  it("finds source Id for reverse operation") {
    withTempFolder { folder =>
      val (sourceId, targetId) = createIds(1, 100)
      val storage = new TaskKeeperLocationStorage(folder)
      storage.store("location1", "location2", Seq((sourceId, targetId)))

      val loaded = storage.loadTasks("location1", "location2")
      loaded.findSourceSystemIdentity(targetId, "location1") shouldBe Some(sourceId)
    }
  }

  def createIds(sourceId: Int, targetId: Int): (TaskId, TaskId) = {
    (TaskId(sourceId.toLong, "task" + sourceId), TaskId(targetId.toLong, "task" + targetId))
  }
}
