#!/usr/bin/env python
import os
from setuptools import setup, find_packages

# Utility function to read the README file.
# Used for the long_description.  It's nice, because now 1) we have a top level
# README file and 2) it's easier to type in the README file than to put a raw
# string in below ...
def read(fname):
    return open(os.path.join(os.path.dirname(__file__), fname)).read()

setup(
    name = "samiru",
    version = "0.0.1",
    author = "Samir Faci",
    author_email = "",
    description = ("Language agnostic tool for packaging of thrift based services and artifacts"),
    license = "Apache License",
    url = "https://github.com/WizeCommerce/samiru",
    #packages=['samiru', 'tests'],
    packages = find_packages(exclude="test"),
    package_data = {'': ['*.yaml']},
    #long_description=read('README.txt'),
    install_requires=['lxml','paramiko','argparse','pyyaml','jinja2'],
    classifiers=[
        "Development Status :: 3 - Alpha",
        "Topic :: Utilities",
        "License :: OSI Approved :: Apache License",
    ],
    entry_points = {
        'console_scripts': ['samiru = samiru.samiru:main']
    },
    zip_safe = True
)
