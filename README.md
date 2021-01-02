# Lung Cancer Diagnosis Extraction
This repository contains a hybrid approach to automatically extracting lung cancer diagnosis from clinical notes written in Spanish.

The approach combines deep learning-based and rule-based methods to improve the lung cancer diagnosis extraction process. Figure 1  shows the proposed approach that integrates three steps: Lung cancer named entity recognition, Negation and speculation detection, and Relating cancer diagnosis and dates. 

<center> <img src="img/approach1.png" width="300" height="400"> </center>

<h4>Lung cancer named entity recognition: </h4>
Contains a deep learning model to extract lung cancer named entities from clinical notes written in Spanish. A BiLSTM-CRF  model is used to carry out named entity recognition.

