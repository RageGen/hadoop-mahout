import random

# Количество точек для генерации
num_points = 100

# Диапазон координат
x_range = (-100, 100)
y_range = (-100, 100)

# Имя файла
file_name = "points.csv"

# Генерация точек
with open(file_name, mode='w') as file:
    for _ in range(num_points):
        x = random.uniform(*x_range)
        y = random.uniform(*y_range)
        file.write(f"{x},{y}\n")

print(f"Файл '{file_name}' успешно создан!")
