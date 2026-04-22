importScripts('/ngsw-worker.js');

self.addEventListener('push', event => {
  const data = event.data.json();
  
  const options = {
    body: data.body
  };

  event.waitUntil(self.registration.showNotification(data.title, options));
});

self.addEventListener('notificationclick', event => {
  event.notification.close();
  
  const urlFixa = '/dashboard'; 

  event.waitUntil(
    clients.openWindow(urlFixa)
  );
});