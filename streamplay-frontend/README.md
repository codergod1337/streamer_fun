# Frontend: React + TypeScript + Vite

##we use some dependencies:
npm install bootstrap
einheitliches Button-Design

npm install react-router-dom
-> Das ist die Routing-Bibliothek für React und dafür zuständig, dass beim Seitenwechsel im Frontend die Seite nicht neu geladen werden muss.

npm install --save-dev @types/node
-> Der Befehl sorgt dafür, dass TypeScript weiß, was process, __dirname, Buffer, global, usw. bedeuten. Ohne @types/node kennt TypeScript diese Begriffe nicht. `const env = loadEnv(mode, process.cwd(), '');`