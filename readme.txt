for train

python train.py -s style_image.jpg -d training_dataset_path -g gpu_id (-1 for CPU )

for generate

python generate.py input_image.jpg -m model.model -o output_image.jpg -g gpu_id