package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.statistics.*;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet
 * loading)
 *
 */
public class CommandDispatcherStats implements CommandDispatcherInterface {
	@Override
	public CommandProcessor dispatch(Commands c, Kernel kernel) {
		switch (c) {
		case RandomElement:
			return new CmdRandomElement(kernel);
		case RandomPolynomial:
			return new CmdRandomPolynomial(kernel);
		case Classes:
			return new CmdClasses(kernel);
		case OrdinalRank:
			return new CmdOrdinalRank(kernel);
		case TiedRank:
			return new CmdTiedRank(kernel);
		case BoxPlot:
			return new CmdBoxPlot(kernel);
		case Histogram:
			return new CmdHistogram(kernel);
		case HistogramRight:
			return new CmdHistogramRight(kernel);
		case DotPlot:
			return new CmdDotPlot(kernel);
		case StemPlot:
			return new CmdStemPlot(kernel);
		case StickGraph:
			return new CmdStickGraph(kernel);
		case StepGraph:
			return new CmdStepGraph(kernel);
		case ResidualPlot:
			return new CmdResidualPlot(kernel);
		case FrequencyPolygon:
			return new CmdFrequencyPolygon(kernel);
		case NormalQuantilePlot:
			return new CmdNormalQuantilePlot(kernel);
		case FrequencyTable:
			return new CmdFrequencyTable(kernel);
		case ContingencyTable:
			return new CmdContingencyTable(kernel);
		case Mean:
		case mean:
			return new CmdMean(kernel);
		case var:
		case Variance:
			return new CmdVariance(kernel);
		case stdevp:
		case SD:
			return new CmdSD(kernel);
		case MAD:
		case mad:
			return new CmdMAD(kernel);
		case SampleVariance:
			return new CmdSampleVariance(kernel);
		case stdev:
		case SampleSD:
			return new CmdSampleSD(kernel);
		case Median:
			return new CmdMedian(kernel);
		case Q1:
		case Quartile1:
			return new CmdQ1(kernel);
		case Q3:
		case Quartile3:
			return new CmdQ3(kernel);
		case Mode:
			return new CmdMode(kernel);
		case SigmaXX:
			return new CmdSigmaXX(kernel);
		case SigmaXY:
			return new CmdSigmaXY(kernel);
		case SigmaYY:
			return new CmdSigmaYY(kernel);
		case cov:
		case Covariance:
			return new CmdCovariance(kernel);
		case SXY:
			return new CmdSXY(kernel);
		case SXX:
			return new CmdSXX(kernel);
		case SYY:
			return new CmdSYY(kernel);
		case MeanX:
			return new CmdMeanX(kernel);
		case MeanY:
			return new CmdMeanY(kernel);

		case CorrelationCoefficient:
		case PMCC:
			return new CmdPMCC(kernel);

		case SampleSDX:
			return new CmdSampleSDX(kernel);
		case SampleSDY:
			return new CmdSampleSDY(kernel);
		case SDX:
			return new CmdSDX(kernel);
		case SDY:
			return new CmdSDY(kernel);

		case FitLine:
		case FitLineY:
			return new CmdFitLineY(kernel);

		case FitLineX:
			return new CmdFitLineX(kernel);
		case FitPoly:
			return new CmdFitPoly(kernel);
		case FitExp:
			return new CmdFitExp(kernel);
		case FitLog:
			return new CmdFitLog(kernel);
		case FitPow:
			return new CmdFitPow(kernel);
		case Fit:
			return new CmdFit(kernel);
		case FitImplicit:
			return new CmdFitImplicit(kernel);
		case FitGrowth:
			return new CmdFitGrowth(kernel);
		case FitSin:
			return new CmdFitSin(kernel);
		case FitLogistic:
			return new CmdFitLogistic(kernel);
		case SumSquaredErrors:
			return new CmdSumSquaredErrors(kernel);
		case RSquare:
			return new CmdRSquare(kernel);
		case Sample:
			return new CmdSample(kernel);
		case Shuffle:
			return new CmdShuffle(kernel);
		case Spearman:
			return new CmdSpearman(kernel);
		case TTest:
			return new CmdTTest(kernel);
		case TTestPaired:
			return new CmdTTestPaired(kernel);
		case TTest2:
			return new CmdTTest2(kernel);
		case TMeanEstimate:
			return new CmdTMeanEstimate(kernel);
		case TMean2Estimate:
			return new CmdTMean2Estimate(kernel);
		case ChiSquaredTest:
			return new CmdChiSquaredTest(kernel);
		case ANOVA:
			return new CmdANOVA(kernel);
		case Percentile:
			return new CmdPercentile(kernel);
		case GeometricMean:
			return new CmdGeometricMean(kernel);
		case HarmonicMean:
			return new CmdHarmonicMean(kernel);
		case RootMeanSquare:
			return new CmdRootMeanSquare(kernel);
		case RandomDiscrete:
			return new CmdRandomDiscrete(kernel);
		case RandomNormal:
			return new CmdRandomNormal(kernel);
		case RandomUniform:
			return new CmdRandomUniform(kernel);
		case RandomBinomial:
			return new CmdRandomBinomial(kernel);
		case RandomPoisson:
			return new CmdRandomPoisson(kernel);
		case Normal:
			return new CmdNormal(kernel);
		case LogNormal:
			return new CmdLogNormal(kernel);
		case InverseLogNormal:
			return new CmdInverseLogNormal(kernel);
		case Logistic:
			return new CmdLogistic(kernel);
		case InverseLogistic:
			return new CmdInverseLogistic(kernel);
		case InverseNormal:
			return new CmdInverseNormal(kernel);
		case BinomialDist:
			return new CmdBinomialDist(kernel);
		case Bernoulli:
			return new CmdBernoulli(kernel);
		case InverseBinomial:
			return new CmdInverseBinomial(kernel);
		case TDistribution:
			return new CmdTDistribution(kernel);
		case InverseTDistribution:
			return new CmdInverseTDistribution(kernel);
		case FDistribution:
			return new CmdFDistribution(kernel);
		case InverseFDistribution:
			return new CmdInverseFDistribution(kernel);
		case Gamma:
			return new CmdGamma(kernel);
		case InverseGamma:
			return new CmdInverseGamma(kernel);
		case Cauchy:
			return new CmdCauchy(kernel);
		case InverseCauchy:
			return new CmdInverseCauchy(kernel);
		case ChiSquared:
			return new CmdChiSquared(kernel);
		case InverseChiSquared:
			return new CmdInverseChiSquared(kernel);
		case Exponential:
			return new CmdExponential(kernel);
		case InverseExponential:
			return new CmdInverseExponential(kernel);
		case HyperGeometric:
			return new CmdHyperGeometric(kernel);
		case InverseHyperGeometric:
			return new CmdInverseHyperGeometric(kernel);
		case Pascal:
			return new CmdPascal(kernel);
		case InversePascal:
			return new CmdInversePascal(kernel);
		case Poisson:
			return new CmdPoisson(kernel);
		case InversePoisson:
			return new CmdInversePoisson(kernel);
		case Weibull:
			return new CmdWeibull(kernel);
		case InverseWeibull:
			return new CmdInverseWeibull(kernel);
		case Zipf:
			return new CmdZipf(kernel);
		case InverseZipf:
			return new CmdInverseZipf(kernel);
		case Triangular:
			return new CmdTriangular(kernel);
		case Uniform:
			return new CmdUniform(kernel);
		case Erlang:
			return new CmdErlang(kernel);
		case CellRange:
			return new CmdCellRange(kernel); // cell range for spreadsheet
												// like A1:A5
		case Row:
			return new CmdRow(kernel);
		case Column:
			return new CmdColumn(kernel);
		case ColumnName:
			return new CmdColumnName(kernel);
		case FillRow:
			return new CmdFillRow(kernel);
		case FillColumn:
			return new CmdFillColumn(kernel);
		case FillCells:
			return new CmdFillCells(kernel);
		case Cell:
			return new CmdCell(kernel);

		case Frequency:
			return new CmdFrequency(kernel);
		case ZProportionTest:
			return new CmdZProportionTest(kernel);
		case ZProportion2Test:
			return new CmdZProportion2Test(kernel);
		case ZProportionEstimate:
			return new CmdZProportionEstimate(kernel);
		case ZProportion2Estimate:
			return new CmdZProportion2Estimate(kernel);
		case ZMeanEstimate:
			return new CmdZMeanEstimate(kernel);
		case ZMean2Estimate:
			return new CmdZMean2Estimate(kernel);
		case ZMeanTest:
			return new CmdZMeanTest(kernel);
		case ZMean2Test:
			return new CmdZMean2Test(kernel);
		default:
			break;
		}
		return null;
	}
}
