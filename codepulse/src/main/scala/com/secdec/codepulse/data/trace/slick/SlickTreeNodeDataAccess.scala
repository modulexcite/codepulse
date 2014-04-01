/*
 * Code Pulse: A real-time code coverage testing tool. For more information
 * see http://code-pulse.com
 *
 * Copyright (C) 2014 Applied Visions - http://securedecisions.avi.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secdec.codepulse.data.trace.slick

import scala.slick.jdbc.JdbcBackend.Database
import com.secdec.codepulse.data.trace.{ TreeNode, TreeNodeDataAccess }

/** Slick-based TreeNodeDataAccess implementation.
  *
  * @author robertf
  */
private[slick] class SlickTreeNodeDataAccess(dao: TreeNodeDataDao, db: Database) extends TreeNodeDataAccess {
	def foreach(f: TreeNode => Unit) {
		iterate { _.foreach(f) }
	}

	def iterate[T](f: Iterator[TreeNode] => T): T = {
		db withSession { implicit session =>
			dao.iterateWith(f)
		}
	}

	def getNode(id: Int): Option[TreeNode] = db withSession { implicit session =>
		dao get id
	}

	def getNode(sig: String): Option[TreeNode] = db withSession { implicit session =>
		dao get sig
	}

	def getNodeId(sig: String): Option[Int] = db withSession { implicit session =>
		dao getId sig
	}

	def foreachMapping(f: (String, Int) => Unit) {
		iterateMappings { _.foreach { case (method, nodeId) => f(method, nodeId) } }
	}

	def iterateMappings[T](f: Iterator[(String, Int)] => T): T = {
		db withSession { implicit session =>
			dao.iterateMappingsWith(f)
		}
	}

	def mapMethodSignature(sig: String, nodeId: Int) = db withTransaction { implicit transaction =>
		dao.storeMethodSignature(sig, nodeId)
	}

	override def mapMethodSignatures(signatures: Iterable[(String, Int)]) = db withTransaction { implicit transaction =>
		dao storeMethodSignatures signatures
	}

	def storeNode(node: TreeNode) = db withTransaction { implicit transaction =>
		dao storeNode node
	}

	override def storeNodes(nodes: Iterable[TreeNode]) = db withTransaction { implicit transaction =>
		dao storeNodes nodes
	}
}