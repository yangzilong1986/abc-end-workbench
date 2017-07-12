import numpy as np
# Load our dataset
from sklearn.datasets import load_iris
#X, y = np.loadtxt("X_classification.txt"), np.loadtxt("y_classification.txt")
dataset = load_iris()
X = dataset.data
y = dataset.target
print(dataset.DESCR)
n_samples, n_features = X.shape

# Compute the mean for each attribute
attribute_means = X.mean(axis=0)
assert attribute_means.shape == (n_features,)
X_d = np.array(X >= attribute_means, dtype='int')

# Now, we split into a training and test set
from sklearn.cross_validation import train_test_split

# Set the random state to the same number to get the same results as in the book
random_state = 14

X_train, X_test, y_train, y_test = train_test_split(X_d, y, random_state=random_state)
print("There are {} training samples".format(y_train.shape))
print("There are {} testing samples".format(y_test.shape))

from collections import defaultdict
from operator import itemgetter


def train(X, y_true, feature):
    """Computes the predictors and error for a given feature using the OneR algorithm

    Parameters
    ----------
    X: array [n_samples, n_features]
        The two dimensional array that holds the dataset. Each row is a sample, each column
        is a feature.

    y_true: array [n_samples,]
        The one dimensional array that holds the class values. Corresponds to X, such that
        y_true[i] is the class value for sample X[i].

    feature: int
        An integer corresponding to the index of the variable we wish to test.
        0 <= variable < n_features

    Returns
    -------
    predictors: dictionary of tuples: (value, prediction)
        For each item in the array, if the variable has a given value, make the given prediction.

    error: float
        The ratio of training data that this rule incorrectly predicts.
    """
    # Check that variable is a valid number
    n_samples, n_features = X.shape
    assert 0 <= feature < n_features
    # Get all of the unique values that this variable has
    values = set(X[:,feature])
    # Stores the predictors array that is returned
    predictors = dict()
    errors = []
    for current_value in values:
        most_frequent_class, error = train_feature_value(X, y_true, feature, current_value)
        predictors[current_value] = most_frequent_class
        errors.append(error)
    # Compute the total error of using this feature to classify on
    total_error = sum(errors)
    return predictors, total_error

# Compute what our predictors say each sample is based on its value
#y_predicted = np.array([predictors[sample[feature]] for sample in X])


def train_feature_value(X, y_true, feature, value):
    # Create a simple dictionary to count how frequency they give certain predictions
    class_counts = defaultdict(int)
    # Iterate through each sample and count the frequency of each class/value pair
    #这个函数有个限制，指定概率的列表必须和元素一一对应，而且和为1，否则这个函数可能不能像预想的那样工作。
    # 稍微解释下，先利用random.uniform()函数生成一个0-1之间的随机数并复制给x，
    # 利用zip()函数将元素和他对应的概率打包成tuple，然后将每个元素的概率进行叠加，直到和大于x终止循环
    for sample, y in zip(X, y_true):
        if sample[feature] == value:
            class_counts[y] += 1
    # Now get the best one by sorting (highest first) and choosing the first item
    sorted_class_counts = sorted(class_counts.items(), key=itemgetter(1), reverse=True)
    most_frequent_class = sorted_class_counts[0][0]
    # The error is the number of samples that do not classify as the most frequent class
    # *and* have the feature value.
    n_samples = X.shape[1]
    error = sum([class_count for class_value, class_count in class_counts.items()
                 if class_value != most_frequent_class])
    return most_frequent_class, error

# Compute all of the predictors
all_predictors = {variable: train(X_train, y_train, variable) for variable in range(X_train.shape[1])}
errors = {variable: error for variable, (mapping, error) in all_predictors.items()}
# Now choose the best and save that as "model"
# Sort by error
best_variable, best_error = sorted(errors.items(), key=itemgetter(1))[0]
print("The best model is based on variable {0} and has error {1:.2f}".format(best_variable, best_error))

# Choose the bset model
model = {'variable': best_variable,
         'predictor': all_predictors[best_variable][0]}
print(model)

def predict(X_test, model):
    variable = model['variable']
    predictor = model['predictor']
    y_predicted = np.array([predictor[int(sample[variable])] for sample in X_test])
    return y_predicted

y_predicted = predict(X_test, model)
print(y_predicted)

# Compute the accuracy by taking the mean of the amounts that y_predicted is equal to y_test
accuracy = np.mean(y_predicted == y_test) * 100
print("The test accuracy is {:.1f}%".format(accuracy))

from sklearn.metrics import classification_report

print(classification_report(y_test, y_predicted))
