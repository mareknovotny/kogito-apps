/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.local.lime;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.utils.DataUtils;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DummyModelsLimeExplainerTest {

    @Test
    void testMapOneFeatureToOutputRegression() {
        for (int seed = 0; seed < 5; seed++) {
            DataUtils.setSeed(seed);
            int idx = 1;
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newNumericalFeature("f1", 100));
            features.add(FeatureFactory.newNumericalFeature("f2", 20));
            features.add(FeatureFactory.newNumericalFeature("f3", 0.1));
            PredictionInput input = new PredictionInput(features);
            PredictionProvider model = TestUtils.getFeaturePassModel(idx);
            List<PredictionOutput> outputs = model.predict(List.of(input));
            Prediction prediction = new Prediction(input, outputs.get(0));

            LimeExplainer limeExplainer = new LimeExplainer(100, 1);
            Saliency saliency = limeExplainer.explain(prediction, model);

            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
    }

    @Test
    void testUnusedFeatureRegression() {
        for (int seed = 0; seed < 5; seed++) {
            DataUtils.setSeed(seed);
            int idx = 2;
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newNumericalFeature("f1", 100));
            features.add(FeatureFactory.newNumericalFeature("f2", 20));
            features.add(FeatureFactory.newNumericalFeature("f3", 10));
            PredictionProvider model = TestUtils.getSumSkipModel(idx);
            PredictionInput input = new PredictionInput(features);
            List<PredictionOutput> outputs = model.predict(List.of(input));
            Prediction prediction = new Prediction(input, outputs.get(0));
            LimeExplainer limeExplainer = new LimeExplainer(1000, 1);
            Saliency saliency = limeExplainer.explain(prediction, model);

            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
    }

    @Test
    void testMapOneFeatureToOutputClassification() {
        for (int seed = 0; seed < 5; seed++) {
            DataUtils.setSeed(seed);
            int idx = 1;
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newNumericalFeature("f1", 1));
            features.add(FeatureFactory.newNumericalFeature("f2", 1));
            features.add(FeatureFactory.newNumericalFeature("f3", 3));
            PredictionInput input = new PredictionInput(features);
            PredictionProvider model = TestUtils.getEvenFeatureModel(idx);
            List<PredictionOutput> outputs = model.predict(List.of(input));
            Prediction prediction = new Prediction(input, outputs.get(0));

            LimeExplainer limeExplainer = new LimeExplainer(1000, 1);
            Saliency saliency = limeExplainer.explain(prediction, model);

            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
    }

    @Test
    void testTextSpamClassification() {
        for (int seed = 0; seed < 5; seed++) {
            DataUtils.setSeed(seed);
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newTextFeature("f1", "we go here and there"));
            features.add(FeatureFactory.newTextFeature("f2", "please give me some money"));
            features.add(FeatureFactory.newTextFeature("f3", "dear friend, please reply"));
            PredictionInput input = new PredictionInput(features);
            PredictionProvider model = TestUtils.getDummyTextClassifier();
            List<PredictionOutput> outputs = model.predict(List.of(input));
            Prediction prediction = new Prediction(input, outputs.get(0));

            LimeExplainer limeExplainer = new LimeExplainer(1000, 1);
            Saliency saliency = limeExplainer.explain(prediction, model);

            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
    }

    @Test
    void testUnusedFeatureClassification() {
        for (int seed = 0; seed < 5; seed++) {
            DataUtils.setSeed(seed);
            int idx = 2;
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newNumericalFeature("f1", 6));
            features.add(FeatureFactory.newNumericalFeature("f2", 3));
            features.add(FeatureFactory.newNumericalFeature("f3", 5));
            PredictionProvider model = TestUtils.getEvenSumModel(idx);
            PredictionInput input = new PredictionInput(features);
            List<PredictionOutput> outputs = model.predict(List.of(input));
            Prediction prediction = new Prediction(input, outputs.get(0));
            LimeExplainer limeExplainer = new LimeExplainer(1000, 1);
            Saliency saliency = limeExplainer.explain(prediction, model);

            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
    }
}