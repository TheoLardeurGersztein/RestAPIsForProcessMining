package org.processmining.plugins.inductiveminer2;

import gnu.trove.set.TIntSet;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduceParameters;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMc.probabilities.Probabilities;
import org.processmining.plugins.InductiveMiner.mining.logs.XLifeCycleClassifier;
import org.processmining.plugins.inductiveminer2.framework.basecases.*;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinder;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Filter;
import org.processmining.plugins.inductiveminer2.framework.fallthroughs.FallThrough;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.*;
import org.processmining.plugins.inductiveminer2.framework.postprocessor.PostProcessor;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfo;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImpl;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.processmining.plugins.inductiveminer2.variants.MiningParametersIM.*;
public class InductiveMinerDefaultParameter extends MiningParametersAbstract {


    public static final List<BaseCaseFinder> filteringBaseCases = Collections
            .unmodifiableList(Arrays.asList(new BaseCaseFinder[] { //
                    new BaseCaseFinderEmptyLog(), //
                    new BaseCaseFinderEmptyTracesFiltering(), //
                    new BaseCaseFinderEmptyTraces(), //
                    new BaseCaseFinderSingleActivityFiltering() }));
    public static final CutFinder filteringCutFinders = new CutFinder() {
        public Cut findCut(IMLog log, IMLogInfo logInfo, MinerState minerState) {
            IMLogInfo logInfoFiltered = Filter.filterNoise(logInfo, minerState.parameters.getNoiseThreshold());

            //call IM cut detection
            return InductiveMiner.findCut(null, logInfoFiltered, MiningParametersIM.basicCutFinders, minerState);
        }
    };

    private static final List<BaseCaseFinder> baseCaseFinders = new ArrayList<>();
    static {
        baseCaseFinders.addAll(filteringBaseCases);
        baseCaseFinders.addAll(basicBaseCaseFinders);
    }


    @Override
    public IMLog getIMLog(XLog xLog) {
        return new IMLogImpl(xLog, getClassifier(), getLifeCycleClassifier());
    }
    @Override
    public XLifeCycleClassifier getLifeCycleClassifier() {
        return MiningParameters.defaultLifeCycleClassifier;
    }
    @Override
    public boolean hasNoise() {
        return false;
    }
    @Override
    public Probabilities getSatProbabilities() {
        return null;
    }

    @Override
    public IMLog2IMLogInfo getLog2LogInfo() {
        return new IMLog2IMLogInfoDefault();
    }

    @Override
    public List<BaseCaseFinder> getBaseCaseFinders() {
        return baseCaseFinders;
    }

    @Override
    public List<CutFinder> getCutFinders() {
        return basicCutFinders;
    }

    @Override
    public List<FallThrough> getFallThroughs() {
        return basicFallThroughs;
    }

    @Override
    public boolean isRepairLifeCycles() {
        return false;
    }

    @Override
    public List<PostProcessor> getPostProcessors() {
        return basicPostProcessors;
    }

    @Override
    public EfficientTreeReduceParameters getReduceParameters() {
        return basicReduceParameters;
    }

    @Override
    public IMLog[] splitLogConcurrent(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
        return LogSplitterConcurrent.split(log, partition, minerState);
    }

    @Override
    public IMLog[] splitLogInterleaved(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
        return LogSplitterInterleavedFiltering.split(log, partition, minerState);
    }

    @Override
    public IMLog[] splitLogLoop(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
        return LogSplitterLoop.split(log, partition, minerState);
    }

    @Override
    public IMLog[] splitLogOr(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
        return LogSplitterOr.split(log, partition, minerState);
    }

    @Override
    public IMLog[] splitLogSequence(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
        return LogSplitterSequenceFiltering.split(log, partition, minerState);
    }

    @Override
    public IMLog[] splitLogXor(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
        return LogSplitterXorFiltering.split(log, partition, minerState);
    }
}
