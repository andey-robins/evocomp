package grammar

type Summary struct {
	Generations           []*Generation          `@@*`
	Best                  *BestEnd               `@@`
	GenerationBestSummary *GenerationBestSummary `@@`
}

type Generation struct {
	Lines []*Line        `@@*`
	End   *GenerationEnd `@@`
}

type Line struct {
	Run        int     `"R" @Int`
	Generation int     `"G" @Int`
	Best       int     `@Int`
	Average    float64 `@Float`
	StdDev     float64 `@Float`
}

type GenerationEnd struct {
	Number     int `@Int`
	NumberTwo  int `@Int`
	RawFitness int `"#"* Ident "-"? Int @Int`
}

type BestEnd struct {
	RawFitness int `"B" "#"* Ident "-"? Int @Int`
}

type GenerationBestSummary struct {
	GenerationLines []*GenerationLine `Ident Ident Ident @@*`
}

type GenerationLine struct {
	Generation  int     `@Int`
	AvgFitness  float64 `@Float`
	BestFitness float64 `@Float`
}
