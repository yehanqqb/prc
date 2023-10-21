[
    {
        "id": "client",
        "order": 1,
        "predicates": [
            {
                "args": {
                    "_genkey_0": "/client/**"
                },
                "name": "Path"
            }
        ],
        "uri": "lb://client",
        "filters": [
            {
                "args": {
                    "_genkey_0": "1"
                },
                "name": "StripPrefix"
            }
        ]
    },
    {
        "id": "supplier-api",
        "order": 1,
        "predicates": [
            {
                "args": {
                    "_genkey_0": "/supplier-api/**"
                },
                "name": "Path"
            }
        ],
        "uri": "lb://supplier-api",
        "filters": [
            {
                "args": {
                    "_genkey_0": "1"
                },
                "name": "StripPrefix"
            }
        ]
    },
    {
        "id": "merchant-api",
        "order": 1,
        "predicates": [
            {
                "args": {
                    "_genkey_0": "/merchant-api/**"
                },
                "name": "Path"
            }
        ],
        "uri": "lb://merchant-api",
        "filters": [
            {
                "args": {
                    "_genkey_0": "1"
                },
                "name": "StripPrefix"
            }
        ]
    }
]