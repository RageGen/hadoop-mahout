import pandas as pd
import matplotlib.pyplot as plt

# Функция для отображения кластеров и сохранения в PDF
def plot_clusters(file_path, title, output_pdf):
    # Чтение данных из файла
    data = pd.read_csv(file_path)
    
    # Отображение данных
    plt.figure(figsize=(8, 6))
    for cluster_id, cluster_data in data.groupby('cluster'):
        plt.scatter(cluster_data['x'], cluster_data['y'], label=f'Cluster {cluster_id}', alpha=0.7)

    plt.title(title)
    plt.xlabel('X-axis')
    plt.ylabel('Y-axis')
    plt.legend()
    plt.grid(True)

    # Сохранение графика в PDF
    plt.savefig(output_pdf, format='pdf')
    print(f"График сохранён в файл: {output_pdf}")
    plt.close()

# Визуализация и сохранение для метрики Squared Euclidean
plot_clusters('lab6/app/output_squared.csv', 'Clusters with Squared Euclidean Distance', 'clusters_squared.pdf')

# Визуализация и сохранение для метрики Manhattan
plot_clusters('lab6/app/output_manhattan.csv', 'Clusters with Manhattan Distance', 'clusters_manhattan.pdf')
