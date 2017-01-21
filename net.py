import math

import numpy as np
import chainer
import chainer.links as L
import chainer.functions as F
from chainer import Variable

class ResidualBlock(chainer.Chain):
    def __init__(self, n_in, n_out, stride=1, ksize=3):
        w = math.sqrt(2)
        super(ResidualBlock, self).__init__(
            c1=L.Convolution2D(n_in, n_out, ksize, stride, 1, w),
            c2=L.Convolution2D(n_out, n_out, ksize, 1, 1, w),
            b1=L.BatchNormalization(n_out),
            b2=L.BatchNormalization(n_out)
        )

    def __call__(self, x, test):
        h = F.relu(self.b1(self.c1(x), test=test))
        h = self.b2(self.c2(h), test=test)
        if x.data.shape != h.data.shape:
            xp = chainer.cuda.get_array_module(x.data)
            n, c, hh, ww = x.data.shape
            pad_c = h.data.shape[1] - c
            p = xp.zeros((n, pad_c, hh, ww), dtype=xp.float32)
            p = chainer.Variable(p, volatile=test)
            x = F.concat((p, x))
            if x.data.shape[2:] != h.data.shape[2:]:
                x = F.average_pooling_2d(x, 1, 2)
        return h + x

class FastStyleNet(chainer.Chain):
    def __init__(self):
        super(FastStyleNet, self).__init__(
            c1=L.Convolution2D(3, 32, 9, stride=1, pad=4),
            c2=L.Convolution2D(32, 64, 4, stride=2, pad=1),
            c3=L.Convolution2D(64, 128, 4,stride=2, pad=1),
            r1=ResidualBlock(128, 128),
            r2=ResidualBlock(128, 128),
            r3=ResidualBlock(128, 128),
            r4=ResidualBlock(128, 128),
            r5=ResidualBlock(128, 128),
            d1=L.Deconvolution2D(128, 64, 4, stride=2, pad=1),
            d2=L.Deconvolution2D(64, 32, 4, stride=2, pad=1),
            d3=L.Deconvolution2D(32, 3, 9, stride=1, pad=4),
            b1=L.BatchNormalization(32),
            b2=L.BatchNormalization(64),
            b3=L.BatchNormalization(128),
            b4=L.BatchNormalization(64),
            b5=L.BatchNormalization(32),
        )

    def __call__(self, x, test=False):
        h = self.b1(F.elu(self.c1(x)), test=test)
        h = self.b2(F.elu(self.c2(h)), test=test)
        h = self.b3(F.elu(self.c3(h)), test=test)
        h = self.r1(h, test=test)
        h = self.r2(h, test=test)
        h = self.r3(h, test=test)
        h = self.r4(h, test=test)
        h = self.r5(h, test=test)
        h = self.b4(F.elu(self.d1(h)), test=test)
        h = self.b5(F.elu(self.d2(h)), test=test)
        y = self.d3(h)
        return (F.tanh(y)+1)*127.5

class VGG(chainer.Chain):
    def __init__(self):
        super(VGG, self).__init__(
            conv1_1=L.Convolution2D(3, 64, 3, stride=1, pad=1),
            conv1_2=L.Convolution2D(64, 64, 3, stride=1, pad=1),

            conv2_1=L.Convolution2D(64, 128, 3, stride=1, pad=1),
            conv2_2=L.Convolution2D(128, 128, 3, stride=1, pad=1),

            conv3_1=L.Convolution2D(128, 256, 3, stride=1, pad=1),
            conv3_2=L.Convolution2D(256, 256, 3, stride=1, pad=1),
            conv3_3=L.Convolution2D(256, 256, 3, stride=1, pad=1),

            conv4_1=L.Convolution2D(256, 512, 3, stride=1, pad=1),
            conv4_2=L.Convolution2D(512, 512, 3, stride=1, pad=1),
            conv4_3=L.Convolution2D(512, 512, 3, stride=1, pad=1),

            conv5_1=L.Convolution2D(512, 512, 3, stride=1, pad=1),
            conv5_2=L.Convolution2D(512, 512, 3, stride=1, pad=1),
            conv5_3=L.Convolution2D(512, 512, 3, stride=1, pad=1)
        )
        self.train = False
        self.mean = np.asarray(120, dtype=np.float32)

    def preprocess(self, image):
        return np.rollaxis(image - self.mean, 2)

    def __call__(self, x):
        conv_1_1 = F.relu(self.conv1_1(x))
        y1 = F.relu(self.conv1_2(conv_1_1))
        pool_1 = F.max_pooling_2d(y1, 2, stride=2)
        conv_2_1 = F.relu(self.conv2_1(pool_1))
        conv_2_2 = F.relu(self.conv2_2(conv_2_1))
        pool2 = F.max_pooling_2d(conv_2_2, 2, stride=2)
        conv_3_1 = F.relu(self.conv3_1(pool2))
        conv_3_2 = F.relu(self.conv3_2(conv_3_1))
        conv_3_3 = F.relu(self.conv3_3(conv_3_2))
        pool3 = F.max_pooling_2d(conv_3_3, 2, stride=2)
        conv_4_1 = F.relu(self.conv4_1(pool3))
        conv_4_2 = F.relu(self.conv4_2(conv_4_1))
        conv_4_3 = F.relu(self.conv4_3(conv_4_2))
        pool4 = F.max_pooling_2d(conv_4_3, 2, stride=2)
        conv_5_1 = F.relu(self.conv5_1(pool4))
        conv_5_2 = F.relu(self.conv5_2(conv_5_1))
        conv_5_3 = F.relu(self.conv5_3(conv_5_2))
        return [conv_1_1, conv_2_1, conv_3_1, conv_4_1, conv_5_1, conv_4_2]
