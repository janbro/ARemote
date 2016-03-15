import tornado.ioloop
import tornado.web
import cPickle as pickle
import serial

pins = {'850c9826-e899-11e5-9ce9-5e5517507c66':'0','850c9bc8-e899-11e5-9ce9-5e5517507c66':'1','850c9d80-e899-11e5-9ce9-5e5517507c66':'3','850c9f88-e899-11e5-9ce9-5e5517507c66':'5','850ca12c-e899-11e5-9ce9-5e5517507c66':'4','850ca29e-e899-11e5-9ce9-5e5517507c66':'2','850ca41a-e899-11e5-9ce9-5e5517507c66':'6','850caa28-e899-11e5-9ce9-5e5517507c66':'7'}
commands = {'On':'255','Off':'0','upC':'+C','dnC':'-C','Preheat to 425':'425','Preheat to 350':'350'}
ser = serial.Serial('COM7', 9600)

with open('data.pk1', 'rb') as fp:
    data = pickle.load(fp)

class MainHandler(tornado.web.RequestHandler):
    def get(self):
        uuid = self.get_arguments("UUID")
        self.write(','.join(data[''+uuid[0]]))

    def post(self):
        data = (pins[self.get_arguments("UUID")[0]]+":"+commands[self.get_arguments("COMMAND")[0].encode('utf-8')])
        print data
        ser.write(data.encode("UTF-8"))
        self.write('ok')

def make_app():
    return tornado.web.Application([
        (r"/", MainHandler),
    ])

if __name__ == "__main__":
    app = make_app()
    app.listen(8000)
    tornado.ioloop.IOLoop.current().start()
