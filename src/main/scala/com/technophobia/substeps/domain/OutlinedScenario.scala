package com.technophobia.substeps.domain

import com.technophobia.substeps.domain.execution.RunResult
import com.technophobia.substeps.domain.repositories.SubstepRepository

class OutlinedScenario(outlineTitle: String, val derivedScenarios: Seq[BasicScenario], tags: Set[Tag]) extends Scenario(outlineTitle, tags) {

  def run(): RunResult = {

    derivedScenarios.foldLeft[RunResult](RunResult.NoneRun)((b, a) => b.combine(a.run()))
  }
}
object OutlinedScenario {


  def apply(substepRepository: SubstepRepository, outlineTitle: String, outline: Seq[String], examples: List[Map[String, String]], tags: Set[Tag]): OutlinedScenario = {

    def applyExampleToSubstepInvocation(example: Map[String, String], outlineLine: String)  = {

      example.foldLeft[String](outlineLine)((b, a) => b.replaceAll("<" + a._1 + ">", a._2))
    }

    val derivedStepsForAllExamples : Seq[Seq[String]] = examples.map((example) => outline.map(applyExampleToSubstepInvocation(example, _)))

    val derivedStepsWithIndexes: Seq[(Seq[String], Int)] = (derivedStepsForAllExamples zip Stream.from(1))

    val derivedScenarios = derivedStepsWithIndexes.map{case (derivedSteps, index) => BasicScenario(substepRepository, outlineTitle + ": " + index, derivedSteps, tags)}

    new OutlinedScenario(outlineTitle, derivedScenarios, tags)
  }
}