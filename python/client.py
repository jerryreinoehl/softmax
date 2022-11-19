import requests


class Softmax():
    def __init__(self, server, name=None, new_run=True):
        self._server = server
        self._name = name
        self._model_id = None
        self._run_id = None

        if name != None:
            self.bind(name)
            if self._model_id == None:
                self.new_model(name)
            if new_run:
                self.new_run()

    @property
    def model_id(self):
        return self._model_id

    @property
    def run_id(self):
        return self._run_id

    def new_model(self, name):
        url = f"{self._server}/api/v1/new-model"
        data = {"name": name}
        response = requests.post(url, json=data)
        response_data = response.json()
        self._model_id = response_data["model_id"]

    def new_run(self, model_id=None):
        if model_id == None:
            model_id = self._model_id

        url = f"{self._server}/api/v1/new-run/{model_id}"
        response = requests.get(url)
        self._run_id = response.json()["run_id"]

    def bind(self, name):
        url = f"{self._server}/api/v1/models"
        response = requests.get(url)
        data = response.json()
        for model in data:
            if model["name"] != None and model["name"].lower() == name.lower():
                self._name = model["name"]
                self._model_id = model["model_id"]
                self._run_id = model["run_id"]
                break

    def log(self, step, name, value, run_id=None):
        if run_id == None:
            run_id = self._run_id

        url = f"{self._server}/api/v1/log/{run_id}"
        data = {"step": step, "data": {name: value}}
        requests.post(url, json=data)

    def get(self, model_id=None):
        if model_id == None:
            model_id = self._model_id

        url = f"{self._server}/api/v1/get/{model_id}"
        response = requests.get(url)
        return response.json()

    def models(self):
        url = f"{self._server}/api/v1/models"
        response = requests.get(url)
        return response.json()

