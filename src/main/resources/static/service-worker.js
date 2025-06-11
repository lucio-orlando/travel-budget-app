const CACHE_NAME = 'travel-budget-cache-v1';
const OFFLINE_URL = '/offline.html';

const FILES_TO_CACHE = [
    OFFLINE_URL,
    '/assets/css/style.css',
    '/assets/css/plugins/bootstrap.min.css',
    '/assets/js/main.js',
    '/assets/js/plugins/bootstrap.min.js',
    '/assets/js/plugins/chart.umd.js',
    '/assets/js/plugins/lucide.js',
    '/assets/img/logo.png'
];

// Install event
self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME).then(cache => {
            return cache.addAll(FILES_TO_CACHE);
        })
    );
    self.skipWaiting();
});

// Activate event
self.addEventListener('activate', event => {
    event.waitUntil(
        caches.keys().then(keys =>
            Promise.all(
                keys.filter(k => k !== CACHE_NAME).map(k => caches.delete(k))
            )
        )
    );
    self.clients.claim();
});

// Fetch event: Serve from cache first, fallback to network, fallback to offline
self.addEventListener('fetch', event => {
    if (event.request.method !== 'GET') return;

    event.respondWith(
        fetch(event.request)
            .then(response => {
                return caches.open(CACHE_NAME).then(cache => {
                    cache.put(event.request, response.clone());
                    return response;
                });
            })
            .catch(() => {
                return caches.match(event.request).then(response => {
                    return response || caches.match(OFFLINE_URL);
                });
            })
    );
});
