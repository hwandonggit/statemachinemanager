package com.fulgent.pipeline.workflow

object test {
   val problem = new Pouring(Vector(4, 7))
   problem.moves
   
   problem.solutions(6)
}