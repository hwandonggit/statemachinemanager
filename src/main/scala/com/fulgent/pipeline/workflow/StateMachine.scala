package com.fulgent.pipeline.workflow

trait StateMachine {
  val statemachine = Set()

//Problem:
//   implement a workflow manager with state machines to provide (re)annotation service
//

//Representation (state and moves):
//   Glass: Int
//   State: Vector[Int] (one entrey per glass)
//   Moves: Empty(glass)
//          Fill(glass)
//          Pour(from, to)
//
//   Solution: find all paths until a state contains our requested quantity
//
//Implem:

  trait Move
  case class Empty(glass: Int) extends Move
  case class Fill(glass: Int) extends Move
  case class Move(frmo: Int, to: Int) extends Move  
  
  val glasses = 0 until capacity.length
  
  val moves = 
    (for (g <- glasses) yield Empty(g)) ++
    (for (g <- glasses) yield Fill(g)) ++
    (for (from <- glasses; to <- glasses if from != to) yield Pour(from, to))  

  class Pouring(capacity: Vector[Int]) {

    // State:
    type State = Vector[Int]
    val initialState = capacity map (x => 0)

    // Moves:
    trait Move {
      def change(state: State): State
    }
    case class Empty(glass: Int) extends Move {
      def change(state: State): State = state updated (glass, 0)
    }
    case class File(glass: Int) extends Move {
      def change(state: State): State = state updated (glass, capacity)
    }
    case class Pour(from: Int, to: Int) extends Move {
      def change(state: State): State = {
        val amount = state(from) min (capacity(to) - state(to))
        state updated (from, state(from) - amount) updated (to, state(to) + amount)
      }
    }

    val glasses = 0 until capacity.length

    val moves = 
      (for (g <- glasses) yield Empty(g)) ++
      (for (g <- glasses) yield Fill(g)) ++
      (for (from <- glasses; to <- glasses if from != to) yield Pour(from, to))

    // optimization: compute end state once
    class Path(history: List[Move], val endState: State) {
      def extend(move: Move) = new Path(move :: history, move change endState)
      override toString() = (history.reverse mkString " ") + "--> " + endState 
    }

    val initialPath = new Path(Nil, initialState)

    def from(paths: Set[Path], explored: Set[Path]): Stream[Set[Path]] = 
      if (paths.isEmpty) Stream.empty
      else {
        val more = for {
          path <- pathes
          next <- moves map path.extend
          // prevent infinite search: do no visit explored state twice
          if !(explored contains next.endState)
        } yield next
        paths #:: from(more, explored ++ (more map (_.endState)))
      }

    val pathSets = from(Set(initialPath), Set(initialState))

    def solution(target: Int): Stream[Path] =
      for {
        pathSet <- pathSets
        path <- pathSet
        if path.endState contains target
      } yield path
  }

//  Note:
//    we could also naked data structures with functions (and not OO methods)
//
//  Guidelines for good design:
//    name everything you can
//    put operations into natural scopes (use private functions, etc...)
//    keep degrees of freedom for future refinements