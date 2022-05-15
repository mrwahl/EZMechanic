#Import the required packages.

import numpy as np
import os

from tflite_model_maker.config import ExportFormat, QuantizationConfig
from tflite_model_maker import model_spec
from tflite_model_maker import object_detector

from tflite_support import metadata

import tensorflow as tf
assert tf.__version__.startswith('2')

tf.get_logger().setLevel('ERROR')
from absl import logging
logging.set_verbosity(logging.ERROR)

#Step 1: Load the dataset
#Images in train_data is used to train the custom object detection model.
#Images in val_data is used to check if the model can generalize well to new images that it hasn't seen before.

train_data = object_detector.DataLoader.from_pascal_voc(
    'parts/train',
    'parts/train',
    ['turbo', 'piston','wheel','mirror']
)

val_data = object_detector.DataLoader.from_pascal_voc(
    'parts/validate',
    'parts/validate',
    ['turbo', 'piston','wheel','mirror']
)

#Select a model architecture
spec = model_spec.get('efficientdet_lite2')

#Train the TensorFlow model with the training data.
model = object_detector.create(train_data, model_spec=spec, batch_size=4, train_whole_model=True, epochs=20, validation_data=val_data)

#evaluate the model
model.evaluate(val_data)
#convert to a tflite mdoel
model.export(export_dir='.', tflite_filename='android.tflite')