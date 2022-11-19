import re
import json
import uuid
import time


class Model():
    class Run():
        def __init__(self):
            self._log = []
            self._steps = {}
            self.timestamp = time.strftime("%a, %b %d %Y %H:%M:%S")

        def log(self, step, data):
            if step in self._steps:
                self._steps[step]["data"].update(data)
            else:
                item = {"step": step, "data": data}
                self._log.append(item)
                self._steps[step] = item

        def get_log(self):
            return self._log

    def __init__(self, id, name):
        self.id = id
        self.name = name
        self.current_run = None
        self._runs = {}

    def add_run(self, id):
        run = self.Run()
        self._runs[id] = run
        self.current_run = id
        return run

    def get_run(self, id):
        run = self._runs[id]
        return {
            "run_id": id,
            "timestamp": run.timestamp,
            "log": run.get_log()
        }

    def get_runs(self):
        return [self.get_run(id) for id in self._runs.keys()]


class SoftmaxApp():
    def __init__(self):
        self.uri_map = [
            ["/api/v1/new-model", self.new_model],
            ["/api/v1/new-run/(?P<id>[0-9a-f]{32})", self.new_run],
            ["/api/v1/models", self.models],
            ["/api/v1/log/(?P<id>[0-9a-f]{32})", self.log],
            ["/api/v1/get/(?P<id>[0-9a-f]{32})", self.get],
        ]

        self._models = {}
        self._runs = {}

        self._compile_uri_map()

    def _compile_uri_map(self):
        for uri in self.uri_map:
            uri[0] = re.compile(uri[0])

    def __call__(self, environ, start_response):
        raw_uri = environ.get("RAW_URI", "/")
        uri_func = self.default
        match = None

        for uri, func in self.uri_map:
            match = uri.match(raw_uri)
            if match:
                environ["match"] = match
                uri_func = func

        if uri_func:
            data = uri_func(environ, start_response)
            print("\x1b[35m", data[0].decode(), "\x1b[0m")
            return data

    def default(self, environ, start_response):
        data = b"default\n"
        status = "200 OK"
        response_headers = [
            ("Content-Type", "text/plain"),
            ("Content-Length", str(len(data))),
        ]
        start_response(status, response_headers)
        return [data]

    def new_model(self, environ, start_response):
        content = self._get_input(environ)
        print(content)

        model_data = json.loads(content)
        model_id = self._new_id()

        model = Model(id=model_id, name=model_data["name"])
        self._models[model_id] = model

        data = json.dumps({"model_id": model_id}).encode()

        status = "200 OK"
        response_headers = [
            ("Content-Type", "application/json"),
            ("Content-Length", str(len(data))),
        ]
        start_response(status, response_headers)
        return [data]

    def new_run(self, environ, start_response):
        id = self._uri_tag("id", environ)
        model = self._get_model_by_id(id)
        run_id = self._new_id()

        run = model.add_run(run_id)
        self._runs[run_id] = run

        print(self._runs)

        data = json.dumps({"run_id": run_id}).encode()

        status = "200 OK"
        response_headers = [
            ("Content-Type", "application/json"),
            ("Content-Length", str(len(data))),
        ]
        start_response(status, response_headers)
        return [data]

    def models(self, environ, start_response):
        data = []
        for model in self._models.values():
            data.append({
                "name": model.name,
                "model_id": model.id,
                "run_id": model.current_run
            })
        data = json.dumps(data).encode()

        status = "200 OK"
        response_headers = [
            ("Content-Type", "application/json"),
            ("Content-Length", str(len(data))),
        ]
        start_response(status, response_headers)
        return [data]

    def log(self, environ, start_response):
        run_id = self._uri_tag("id", environ)
        run = self._get_run_by_id(run_id)
        content = self._get_input(environ)

        log_data = json.loads(content)
        run.log(log_data["step"], log_data["data"])

        data = b""
        status = "200 OK"
        response_headers = [
            ("Content-Type", "application/json"),
            ("Content-Length", str(len(data))),
        ]
        start_response(status, response_headers)
        return [data]

    def get(self, environ, start_response):
        id = self._uri_tag("id", environ)
        model = self._models[id]

        data = {
            "model_id": model.id,
            "name": model.name,
            "run_id": model.current_run,
            "runs": model.get_runs()
        }
        data = json.dumps(data).encode()

        status = "200 OK"
        response_headers = [
            ("Content-Type", "application/json"),
            ("Content-Length", str(len(data))),
        ]
        start_response(status, response_headers)
        return [data]

    def _new_id(self):
        return uuid.uuid4().hex

    def _get_input(self, environ):
        return environ["wsgi.input"].read().decode()

    def _uri_tag(self, tag, environ):
        return environ["match"][tag]

    def _get_model_by_id(self, id):
        return self._models[id]

    def _get_run_by_id(self, id):
        return self._runs[id]


def create_app():
    app = SoftmaxApp()
    return app
