package main

import (
	"bytes"
	"flag"
	"fmt"
	"os"

	"github.com/alecthomas/participle/v2"
	"github.com/andey-robins/evocomp-parser/grammar"
)

func pythonPrintListInts(ll []int) string {
	out := "["
	for i, l := range ll {
		out += fmt.Sprintf("%d", l)
		if i != len(ll)-1 {
			out += ", "
		}
	}
	out += "]"
	return out
}

func pythonPrintListFloats(ll []float64) string {
	out := "["
	for i, l := range ll {
		out += fmt.Sprintf("%.3f", l)
		if i != len(ll)-1 {
			out += ", "
		}
	}
	out += "]"
	return out
}

func main() {

	var lines int
	var fname string
	flag.IntVar(&lines, "l", 16, "Number of lines to discard")
	flag.StringVar(&fname, "f", "", "File to parse")
	flag.Parse()

	parser, err := participle.Build[grammar.Summary]()
	if err != nil {
		panic(err)
	}

	fileBytes, err := os.ReadFile(fname)
	if err != nil {
		panic(err)
	}

	headerlessLines := bytes.Split(fileBytes, []byte{'\n'})[lines:]
	fileContents := bytes.Join(headerlessLines, []byte{'\n'})

	summary, err := parser.ParseString("", string(fileContents))
	if err != nil {
		panic(err)
	}

	out := "{"

	for i, generation := range summary.Generations {
		bestFitnesses := make([]int, 0)
		avgFitnesses := make([]float64, 0)

		for _, line := range generation.Lines {
			bestFitnesses = append(bestFitnesses, line.Best)
			avgFitnesses = append(avgFitnesses, line.Average)
		}

		out += fmt.Sprintf("%d: {'best': %s, 'avg': %s},", i, pythonPrintListInts(bestFitnesses), pythonPrintListFloats(avgFitnesses))
	}

	out += "}"
	fmt.Println(out)
}
