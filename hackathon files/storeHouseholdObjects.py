import cPickle as pickle

data = {'850c9826-e899-11e5-9ce9-5e5517507c66':['On','Off','+ Volume','- Volume'],'850c9bc8-e899-11e5-9ce9-5e5517507c66':['On','Off','upC','dnC'],'850c9d80-e899-11e5-9ce9-5e5517507c66':['On','Off'],'850c9f88-e899-11e5-9ce9-5e5517507c66':['Check messages','Answer'],'850ca12c-e899-11e5-9ce9-5e5517507c66':['On','Off'],'850ca29e-e899-11e5-9ce9-5e5517507c66':['Off','Preheat to 350','Preheat to 425'],'850ca41a-e899-11e5-9ce9-5e5517507c66':['Brew','Stop'],'850caa28-e899-11e5-9ce9-5e5517507c66':['On','Off','Launch Netflix']}
print data['850c9f88-e899-11e5-9ce9-5e5517507c66']
with open('data.pk1', 'wb') as fp:
    pickle.dump(data, fp, pickle.HIGHEST_PROTOCOL)
