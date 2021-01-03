# Lung Cancer Diagnosis Extraction
This repository contains a hybrid approach for automatically extracting lung cancer diagnosis from clinical notes written in Spanish.

The approach combines deep learning-based and rule-based methods to improve the lung cancer diagnosis extraction process. Figure 1  shows the proposed approach that integrates three steps: Lung cancer named entity recognition</a>, Negation and speculation detection, and Relating cancer diagnosis and dates. For each of these steps, a directory containing the source code has been created.

<center> <img src="img/approach1.png" width="500" height="700"> </center>

<h4> 1. Lung cancer named entity recognition: </h4>
This step describes a deep learning model to extract lung cancer named entities from clinical notes written in Spanish. A BiLSTM-CRF  model is used to carry out named entity recognition.

<h4> 2. Negation and speculation detection: </h4>
Detecting speculation and negation is a crucial step to extract the cancer diagnosis correctly. This section shows a rule-based approach to detect negation and speculation in clinical texts written in Spanish

<h4> 3. Relating cancer diagnosis and dates: </h4>
Once lung cancer named entities have been extracted, and negation and speculation detection has been solved; the only task that is needed is to extract the cancer diagnosis is relating cancer entities to dates. In this step the cancer diagnosis is linked to the proper diagnos date.
